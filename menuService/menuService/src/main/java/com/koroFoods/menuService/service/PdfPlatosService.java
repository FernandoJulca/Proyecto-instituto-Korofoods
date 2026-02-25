package com.koroFoods.menuService.service;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.koroFoods.menuService.dto.ReportePlatoItem;
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
public class PdfPlatosService {

	private static final DeviceRgb COLOR_PRIMARY   = new DeviceRgb(13, 110, 253);
    private static final DeviceRgb COLOR_SUCCESS   = new DeviceRgb(25, 135, 84);
    private static final DeviceRgb COLOR_WARNING   = new DeviceRgb(255, 193, 7);
    private static final DeviceRgb COLOR_DANGER    = new DeviceRgb(220, 53, 69);
    private static final DeviceRgb COLOR_SECONDARY = new DeviceRgb(108, 117, 125);
    private static final DeviceRgb COLOR_INFO      = new DeviceRgb(13, 202, 240);

    public byte[] generarReportePlatos(List<ReportePlatoItem> platos) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4.rotate());
            document.setMargins(20, 20, 20, 20);

            agregarEncabezado(document);
            agregarEstadisticas(document, platos);
            agregarTabla(document, platos);

            document.close();
            log.info("PDF de platos generado con {} registros", platos.size());
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error al generar PDF de platos", e);
            throw new RuntimeException("Error al generar el reporte PDF", e);
        }
    }

    private void agregarEncabezado(Document document) {
        document.add(new Paragraph("KOROFOOD")
                .setFontSize(24).setBold()
                .setFontColor(COLOR_PRIMARY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5));

        document.add(new Paragraph("Reporte de Platos / Menú")
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

    private void agregarEstadisticas(Document document, List<ReportePlatoItem> platos) {
        long totalActivos   = platos.stream().filter(p -> Boolean.TRUE.equals(p.getActivo())).count();
        long totalInactivos = platos.stream().filter(p -> !Boolean.TRUE.equals(p.getActivo())).count();
        long entradas  = platos.stream().filter(p -> "E".equals(p.getTipoPlato())).count();
        long sopas     = platos.stream().filter(p -> "S".equals(p.getTipoPlato())).count();
        long principales = platos.stream().filter(p -> "P".equals(p.getTipoPlato())).count();
        long bebidas   = platos.stream().filter(p -> "B".equals(p.getTipoPlato())).count();

        float[] widths = {1, 1, 1, 1, 1, 1};
        Table stats = new Table(UnitValue.createPercentArray(widths));
        stats.setWidth(UnitValue.createPercentValue(100)).setMarginBottom(20);

        agregarStatCard(stats, "TOTAL PLATOS",    String.valueOf(platos.size()),   COLOR_PRIMARY);
        agregarStatCard(stats, "ACTIVOS",         String.valueOf(totalActivos),    COLOR_SUCCESS);
        agregarStatCard(stats, "INACTIVOS",       String.valueOf(totalInactivos),  COLOR_DANGER);
        agregarStatCard(stats, "ENTRADAS",        String.valueOf(entradas),        COLOR_INFO);
        agregarStatCard(stats, "PLATOS PPAL.",    String.valueOf(principales),     COLOR_WARNING);
        agregarStatCard(stats, "BEBIDAS",         String.valueOf(bebidas),         COLOR_SECONDARY);

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

    private void agregarTabla(Document document, List<ReportePlatoItem> platos) {
        document.add(new Paragraph("Detalle de Platos")
                .setFontSize(14).setBold().setMarginBottom(10));

        float[] columnWidths = {0.5f, 2.5f, 1.5f, 1.2f, 1f, 1.5f, 1f};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        agregarHeaderCell(table, "N°");
        agregarHeaderCell(table, "Nombre");
        agregarHeaderCell(table, "Tipo");
        agregarHeaderCell(table, "Precio");
        agregarHeaderCell(table, "Stock");
        agregarHeaderCell(table, "Etiquetas");
        agregarHeaderCell(table, "Estado");

        int numero = 1;
        for (ReportePlatoItem p : platos) {
            agregarDataCell(table, String.valueOf(numero++), TextAlignment.CENTER);
            agregarDataCell(table, p.getNombre(), TextAlignment.LEFT);
            agregarDataCell(table, p.getTipoPlatoDescripcion(), TextAlignment.CENTER);
            agregarDataCell(table, "S/ " + p.getPrecio().setScale(2, RoundingMode.HALF_UP), TextAlignment.RIGHT);

            // Stock con color según cantidad
            DeviceRgb colorStock = p.getStock() <= 5 ? COLOR_DANGER
                    : p.getStock() <= 15 ? COLOR_WARNING
                    : COLOR_SUCCESS;
            table.addCell(new Cell()
                    .add(new Paragraph(String.valueOf(p.getStock()))
                            .setFontSize(8).setBold().setFontColor(colorStock))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setPadding(5)
                    .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f)));

            // Etiquetas
            String etiquetas = p.getEtiquetas() != null && !p.getEtiquetas().isEmpty()
                    ? String.join(", ", p.getEtiquetas())
                    : "-";
            agregarDataCell(table, etiquetas, TextAlignment.CENTER);

            // Estado activo/inactivo
            boolean activo = Boolean.TRUE.equals(p.getActivo());
            table.addCell(new Cell()
                    .add(new Paragraph(activo ? "Activo" : "Inactivo")
                            .setFontSize(8).setBold().setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(activo ? COLOR_SUCCESS : COLOR_DANGER)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setPadding(5));
        }

        document.add(table);

        document.add(new Paragraph("Total de platos: " + platos.size())
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

    private String obtenerTipoDescripcion(String tipo) {
        return switch (tipo) {
            case "E" -> "Entrada";
            case "S" -> "Sopa";
            case "P" -> "Plato Principal";
            case "B" -> "Bebida";
            default  -> tipo;
        };
    }
}
