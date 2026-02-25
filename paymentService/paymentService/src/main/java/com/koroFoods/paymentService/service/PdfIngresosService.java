package com.koroFoods.paymentService.service;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
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
import com.koroFoods.paymentService.dtos.ReporteIngresoItem;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;



import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PdfIngresosService {


    private static final DeviceRgb COLOR_PRIMARY   = new DeviceRgb(13, 110, 253);
    private static final DeviceRgb COLOR_HEADER    = new DeviceRgb(248, 249, 250);
    private static final DeviceRgb COLOR_SUCCESS   = new DeviceRgb(25, 135, 84);
    private static final DeviceRgb COLOR_WARNING   = new DeviceRgb(255, 193, 7);
    private static final DeviceRgb COLOR_DANGER    = new DeviceRgb(220, 53, 69);
    private static final DeviceRgb COLOR_SECONDARY = new DeviceRgb(108, 117, 125);
    private static final DeviceRgb COLOR_INFO      = new DeviceRgb(13, 202, 240);

    public byte[] generarReporteIngresos(List<ReporteIngresoItem> pagos) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4.rotate()); // horizontal por más columnas
            document.setMargins(20, 20, 20, 20);

            agregarEncabezado(document);
            agregarEstadisticas(document, pagos);
            agregarTabla(document, pagos);

            document.close();
            log.info("PDF de ingresos generado con {} registros", pagos.size());
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error al generar PDF de ingresos", e);
            throw new RuntimeException("Error al generar el reporte PDF", e);
        }
    }

    private void agregarEncabezado(Document document) {
        document.add(new Paragraph("KOROFOOD")
                .setFontSize(24).setBold()
                .setFontColor(COLOR_PRIMARY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5));

        document.add(new Paragraph("Reporte de Ingresos")
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

    private void agregarEstadisticas(Document document, List<ReporteIngresoItem> pagos) {
        // Calcular totales
        BigDecimal totalIngresos = pagos.stream()
                .filter(p -> "PAG".equals(p.getEstado()))
                .map(ReporteIngresoItem::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalPagados  = pagos.stream().filter(p -> "PAG".equals(p.getEstado())).count();
        long totalPendientes = pagos.stream().filter(p -> "PEN".equals(p.getEstado())).count();
        long totalAnulados = pagos.stream().filter(p -> "ANU".equals(p.getEstado()) || "RECH".equals(p.getEstado())).count();

        BigDecimal totalReservas = pagos.stream()
                .filter(p -> "PAG".equals(p.getEstado()) && "Depósito Reserva".equals(p.getTipoPago()))
                .map(ReporteIngresoItem::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPedidos = pagos.stream()
                .filter(p -> "PAG".equals(p.getEstado()) && "Pago Pedido".equals(p.getTipoPago()))
                .map(ReporteIngresoItem::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Fila 1: Totales principales
        float[] widths = {1, 1, 1, 1, 1};
        Table stats = new Table(UnitValue.createPercentArray(widths));
        stats.setWidth(UnitValue.createPercentValue(100)).setMarginBottom(20);

        agregarStatCard(stats, "TOTAL INGRESOS",   "S/ " + totalIngresos.setScale(2, RoundingMode.HALF_UP), COLOR_PRIMARY);
        agregarStatCard(stats, "PAGADOS",          String.valueOf(totalPagados),   COLOR_SUCCESS);
        agregarStatCard(stats, "PENDIENTES",       String.valueOf(totalPendientes), COLOR_WARNING);
        agregarStatCard(stats, "ANULADOS/RECHAZ.", String.valueOf(totalAnulados),  COLOR_DANGER);
        agregarStatCard(stats, "TOTAL REGISTROS",  String.valueOf(pagos.size()),   COLOR_SECONDARY);

        document.add(stats);

        // Fila 2: Desglose por tipo
        float[] widths2 = {1, 1};
        Table stats2 = new Table(UnitValue.createPercentArray(widths2));
        stats2.setWidth(UnitValue.createPercentValue(100)).setMarginBottom(20);

        agregarStatCard(stats2, "INGRESOS POR RESERVAS", "S/ " + totalReservas.setScale(2, RoundingMode.HALF_UP), COLOR_INFO);
        agregarStatCard(stats2, "INGRESOS POR PEDIDOS",  "S/ " + totalPedidos.setScale(2, RoundingMode.HALF_UP),  COLOR_SUCCESS);

        document.add(stats2);
    }

    private void agregarStatCard(Table table, String label, String value, DeviceRgb color) {
        table.addCell(new Cell()
                .add(new Paragraph(label).setFontSize(9).setBold().setFontColor(color).setTextAlignment(TextAlignment.CENTER))
                .add(new Paragraph(value).setFontSize(16).setBold().setTextAlignment(TextAlignment.CENTER))
                .setBackgroundColor(new DeviceRgb(248, 249, 250))
                .setBorder(new SolidBorder(color, 1))
                .setPadding(10));
    }

    private void agregarTabla(Document document, List<ReporteIngresoItem> pagos) {
        document.add(new Paragraph("Detalle de Ingresos")
                .setFontSize(14).setBold().setMarginBottom(10));

        float[] columnWidths = {0.8f, 2f, 1.5f, 1.5f, 1.5f, 1.2f, 1.5f};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        agregarHeaderCell(table, "N°");
        agregarHeaderCell(table, "Referencia");
        agregarHeaderCell(table, "Tipo");
        agregarHeaderCell(table, "Método");
        agregarHeaderCell(table, "Monto");
        agregarHeaderCell(table, "Estado");
        agregarHeaderCell(table, "Fecha Pago");
       

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        int numero = 1;

        for (ReporteIngresoItem p : pagos) {
            agregarDataCell(table, String.valueOf(numero++), TextAlignment.CENTER);
            agregarDataCell(table, p.getReferenciaPago(), TextAlignment.CENTER);
            agregarDataCell(table, p.getTipoPago(), TextAlignment.CENTER);
            agregarDataCell(table, p.getMetodoPago(), TextAlignment.CENTER);
            agregarDataCell(table, "S/ " + (p.getMonto() != null ? p.getMonto().setScale(2, RoundingMode.HALF_UP) : "0.00"), TextAlignment.RIGHT);

            // Estado con color
            DeviceRgb colorEstado = obtenerColorEstado(p.getEstado());
            table.addCell(new Cell()
                    .add(new Paragraph(p.getEstadoDescripcion())
                            .setFontSize(8).setBold().setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(colorEstado)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setPadding(5));

            agregarDataCell(table,
                    p.getFechaPago() != null ? p.getFechaPago().format(formatter) : "-",
                    TextAlignment.CENTER);
            
        }

        document.add(table);

        // Total al pie
        BigDecimal totalFinal = pagos.stream()
                .filter(p -> "PAG".equals(p.getEstado()))
                .map(ReporteIngresoItem::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        document.add(new Paragraph("Total ingresos confirmados: S/ " + totalFinal.setScale(2, RoundingMode.HALF_UP))
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

    private DeviceRgb obtenerColorEstado(String estado) {
        return switch (estado) {
            case "PAG"  -> COLOR_SUCCESS;
            case "PEN"  -> COLOR_WARNING;
            case "ANU"  -> COLOR_DANGER;
            case "RECH" -> COLOR_DANGER;
            case "EXP"  -> COLOR_SECONDARY;
            default     -> COLOR_SECONDARY;
        };
    }
}
