package com.koroFoods.eventService.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.koroFoods.eventService.dtos.ReporteEventoItem;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class PdfEventosService {

	private static final DeviceRgb COLOR_PRIMARY   = new DeviceRgb(13, 110, 253);
    private static final DeviceRgb COLOR_SUCCESS   = new DeviceRgb(25, 135, 84);
    private static final DeviceRgb COLOR_WARNING   = new DeviceRgb(255, 193, 7);
    private static final DeviceRgb COLOR_DANGER    = new DeviceRgb(220, 53, 69);
    private static final DeviceRgb COLOR_SECONDARY = new DeviceRgb(108, 117, 125);
    private static final DeviceRgb COLOR_INFO      = new DeviceRgb(13, 202, 240);

    public byte[] generarReporteEventos(List<ReporteEventoItem> eventos) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4.rotate());
            document.setMargins(20, 20, 20, 20);

            agregarEncabezado(document);
            agregarEstadisticas(document, eventos);
            agregarTabla(document, eventos);

            document.close();
            log.info("PDF de eventos generado con {} registros", eventos.size());
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error al generar PDF de eventos", e);
            throw new RuntimeException("Error al generar el reporte PDF", e);
        }
    }

    private void agregarEncabezado(Document document) {
        document.add(new Paragraph("KOROFOOD")
                .setFontSize(24).setBold()
                .setFontColor(COLOR_PRIMARY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5));

        document.add(new Paragraph("Reporte de Eventos")
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5));

        document.add(new Paragraph("Fecha de generación: " +
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .setFontSize(10)
                .setFontColor(COLOR_SECONDARY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));
    }

    private void agregarEstadisticas(Document document, List<ReporteEventoItem> eventos) {
        long proximos    = eventos.stream().filter(e -> "Próximo".equals(e.getEstado())).count();
        long enCurso     = eventos.stream().filter(e -> "En curso".equals(e.getEstado())).count();
        long finalizados = eventos.stream().filter(e -> "Finalizado".equals(e.getEstado())).count();
        long activos     = eventos.stream().filter(e -> Boolean.TRUE.equals(e.getActivo())).count();

        BigDecimal costoTotal = eventos.stream()
                .map(ReporteEventoItem::getCosto)
                .filter(c -> c != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        float[] widths = {1, 1, 1, 1, 1};
        Table stats = new Table(UnitValue.createPercentArray(widths));
        stats.setWidth(UnitValue.createPercentValue(100)).setMarginBottom(20);

        agregarStatCard(stats, "TOTAL EVENTOS",  String.valueOf(eventos.size()),  COLOR_PRIMARY);
        agregarStatCard(stats, "PRÓXIMOS",       String.valueOf(proximos),        COLOR_INFO);
        agregarStatCard(stats, "EN CURSO",       String.valueOf(enCurso),         COLOR_SUCCESS);
        agregarStatCard(stats, "FINALIZADOS",    String.valueOf(finalizados),     COLOR_SECONDARY);
        agregarStatCard(stats, "COSTO TOTAL",    "S/ " + costoTotal.setScale(2, RoundingMode.HALF_UP), COLOR_WARNING);

        document.add(stats);
    }

    private void agregarStatCard(Table table, String label, String value, DeviceRgb color) {
        table.addCell(new Cell()
                .add(new Paragraph(label).setFontSize(9).setBold().setFontColor(color).setTextAlignment(TextAlignment.CENTER))
                .add(new Paragraph(value).setFontSize(16).setBold().setTextAlignment(TextAlignment.CENTER))
                .setBackgroundColor(new DeviceRgb(248, 249, 250))
                .setBorder(new SolidBorder(color, 1))
                .setPadding(10));
    }

    private void agregarTabla(Document document, List<ReporteEventoItem> eventos) {
        document.add(new Paragraph("Detalle de Eventos")
                .setFontSize(14).setBold().setMarginBottom(10));

        float[] columnWidths = {0.5f, 2f, 1.5f, 1.5f, 1.8f, 1.8f, 1.2f, 1.2f};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        agregarHeaderCell(table, "N°");
        agregarHeaderCell(table, "Nombre");
        agregarHeaderCell(table, "Temática");
        agregarHeaderCell(table, "Descripción");
        agregarHeaderCell(table, "Fecha Inicio");
        agregarHeaderCell(table, "Fecha Fin");
        agregarHeaderCell(table, "Costo");
        agregarHeaderCell(table, "Estado");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        int numero = 1;

        for (ReporteEventoItem e : eventos) {
            agregarDataCell(table, String.valueOf(numero++), TextAlignment.CENTER);
            agregarDataCell(table, e.getNombre(), TextAlignment.LEFT);
            agregarDataCell(table, e.getTematica() != null ? e.getTematica() : "-", TextAlignment.CENTER);

            // Descripción truncada
            String desc = e.getDescripcion() != null && e.getDescripcion().length() > 60
                    ? e.getDescripcion().substring(0, 60) + "..."
                    : (e.getDescripcion() != null ? e.getDescripcion() : "-");
            agregarDataCell(table, desc, TextAlignment.LEFT);

            agregarDataCell(table,
                    e.getFechaInicio() != null ? e.getFechaInicio().format(formatter) : "-",
                    TextAlignment.CENTER);
            agregarDataCell(table,
                    e.getFechaFin() != null ? e.getFechaFin().format(formatter) : "-",
                    TextAlignment.CENTER);
            agregarDataCell(table,
                    e.getCosto() != null ? "S/ " + e.getCosto().setScale(2, RoundingMode.HALF_UP) : "-",
                    TextAlignment.RIGHT);

            // Estado con color
            DeviceRgb colorEstado = switch (e.getEstado()) {
                case "Próximo"    -> COLOR_INFO;
                case "En curso"   -> COLOR_SUCCESS;
                case "Finalizado" -> COLOR_SECONDARY;
                default           -> COLOR_SECONDARY;
            };
            table.addCell(new Cell()
                    .add(new Paragraph(e.getEstado())
                            .setFontSize(8).setBold().setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(colorEstado)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setPadding(5));
        }

        document.add(table);

        document.add(new Paragraph("Total de eventos: " + eventos.size())
                .setFontSize(12).setBold()
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(15));
    }

    private void agregarHeaderCell(Table table, String texto) {
        table.addHeaderCell(new Cell()
                .add(new Paragraph(texto).setFontSize(9).setBold().setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(COLOR_PRIMARY)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(7));
    }

    private void agregarDataCell(Table table, String texto, TextAlignment alignment) {
        table.addCell(new Cell()
                .add(new Paragraph(texto != null ? texto : "-").setFontSize(8))
                .setTextAlignment(alignment)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(5)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f)));
    }
}
