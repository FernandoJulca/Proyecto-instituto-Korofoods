package com.koroFoods.reservationService.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
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
import com.koroFoods.reservationService.dto.ReporteReservaItem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j

public class PdfReservaService {

	// Colores corporativos
    private static final DeviceRgb COLOR_PRIMARY = new DeviceRgb(13, 110, 253); // Azul
    private static final DeviceRgb COLOR_HEADER = new DeviceRgb(248, 249, 250); // Gris claro
    private static final DeviceRgb COLOR_SUCCESS = new DeviceRgb(25, 135, 84); // Verde
    private static final DeviceRgb COLOR_WARNING = new DeviceRgb(255, 193, 7); // Amarillo
    private static final DeviceRgb COLOR_DANGER = new DeviceRgb(220, 53, 69); // Rojo
    private static final DeviceRgb COLOR_SECONDARY = new DeviceRgb(108, 117, 125); // Gris

    public byte[] generarReporteReservas(List<ReporteReservaItem> reservas, 
                                         LocalDate fechaInicio, 
                                         LocalDate fechaFin) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // ========== ENCABEZADO ==========
            agregarEncabezado(document, fechaInicio, fechaFin);

            // ========== ESTADÍSTICAS RESUMIDAS ==========
            agregarEstadisticas(document, reservas);

            // ========== TABLA DE RESERVAS ==========
            agregarTablaReservas(document, reservas);

            document.close();
            
            log.info("✅ PDF de reservas generado exitosamente con {} registros", reservas.size());
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("❌ Error al generar PDF de reservas", e);
            throw new RuntimeException("Error al generar el reporte PDF", e);
        }
    }

    private void agregarEncabezado(Document document, LocalDate fechaInicio, LocalDate fechaFin) {
        // Logo/Nombre del restaurante
        Paragraph titulo = new Paragraph("KOROFOOD")
                .setFontSize(24)
                .setBold()
                .setFontColor(COLOR_PRIMARY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
        document.add(titulo);

        Paragraph subtitulo = new Paragraph("Reporte de Reservas")
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(subtitulo);

        // Tabla de información del reporte
        Table infoTable = new Table(2);
        infoTable.setWidth(UnitValue.createPercentValue(100));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        agregarInfoRow(infoTable, "Período:", 
            fechaInicio.format(formatter) + " - " + fechaFin.format(formatter));
        agregarInfoRow(infoTable, "Fecha de generación:", 
            LocalDate.now().format(formatter));
        
        infoTable.setMarginBottom(20);
        document.add(infoTable);
    }

    private void agregarInfoRow(Table table, String label, String value) {
        table.addCell(new Cell()
            .add(new Paragraph(label).setBold())
            .setBorder(null)
            .setPadding(5));
        table.addCell(new Cell()
            .add(new Paragraph(value))
            .setBorder(null)
            .setPadding(5));
    }

    private void agregarEstadisticas(Document document, List<ReporteReservaItem> reservas) {
        // Contar por estado (usando el estado en mayúsculas)
        Map<String, Long> porEstado = reservas.stream()
            .collect(Collectors.groupingBy(
                ReporteReservaItem::getEstado, // ✅ CAMBIAR: usar estado en lugar de estadoDescripcion
                Collectors.counting()
            ));

        // Tabla de 4 columnas para estadísticas
        float[] columnWidths = {1, 1, 1, 1};
        Table statsTable = new Table(UnitValue.createPercentArray(columnWidths));
        statsTable.setWidth(UnitValue.createPercentValue(100));
        statsTable.setMarginBottom(20);

        // Confirmadas
        agregarStatCard(statsTable, "CONFIRMADAS", 
            porEstado.getOrDefault("CONFIRMADA", 0L).toString(), // ✅ MAYÚSCULAS
            COLOR_SUCCESS);

        // Pendientes
        agregarStatCard(statsTable, "PENDIENTES", 
            porEstado.getOrDefault("PENDIENTE", 0L).toString(), // ✅ MAYÚSCULAS
            COLOR_WARNING);

        // Canceladas
        agregarStatCard(statsTable, "CANCELADAS", 
            porEstado.getOrDefault("CANCELADA", 0L).toString(), // ✅ MAYÚSCULAS
            COLOR_DANGER);

        // Completadas
        agregarStatCard(statsTable, "COMPLETADAS", 
            porEstado.getOrDefault("COMPLETADA", 0L).toString(), // ✅ MAYÚSCULAS
            COLOR_SECONDARY);

        document.add(statsTable);
    }

    private void agregarStatCard(Table table, String label, String value, DeviceRgb color) {
        Cell cell = new Cell()
            .add(new Paragraph(label)
                .setFontSize(10)
                .setBold()
                .setFontColor(color)
                .setTextAlignment(TextAlignment.CENTER))
            .add(new Paragraph(value)
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER))
            .setBackgroundColor(new DeviceRgb(248, 249, 250))
            .setBorder(new SolidBorder(color, 1))
            .setPadding(10)
            .setTextAlignment(TextAlignment.CENTER)
            .setVerticalAlignment(VerticalAlignment.MIDDLE);
        
        table.addCell(cell);
    }

    private void agregarTablaReservas(Document document, List<ReporteReservaItem> reservas) {
        // Título de la tabla
        Paragraph tituloTabla = new Paragraph("Detalle de Reservas")
                .setFontSize(14)
                .setBold()
                .setMarginBottom(10);
        document.add(tituloTabla);

        // Tabla con 5 columnas
        float[] columnWidths = {1f, 2f, 2.5f, 1.5f};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // Headers con estilo
        agregarHeaderCell(table, "N°");
        agregarHeaderCell(table, "Mesa/Zona");
        agregarHeaderCell(table, "Fecha y Hora");
        agregarHeaderCell(table, "Estado");

        // Datos
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (ReporteReservaItem item : reservas) {
            // ID
            agregarDataCell(table, item.getIdReserva().toString(), TextAlignment.CENTER);
            
            
            // Mesa/Zona
            String mesaZona = String.format("Mesa %d (%s)", 
                item.getNumeroMesa(), 
                item.getZona());
            agregarDataCell(table, mesaZona, TextAlignment.CENTER);
            
            // Fecha/Hora
            agregarDataCell(table, item.getFechaHora().format(formatter), TextAlignment.CENTER);
            
            // Estado con color
            DeviceRgb colorEstado = obtenerColorEstado(item.getEstado());
            Cell cellEstado = new Cell()
                .add(new Paragraph(item.getEstadoDescripcion())
                    .setFontSize(9)
                    .setBold()
                    .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(colorEstado)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(5);
            table.addCell(cellEstado);
        }

        document.add(table);

        // Total
        Paragraph total = new Paragraph(String.format("\nTotal de reservas: %d", reservas.size()))
                .setFontSize(12)
                .setBold()
                .setMarginTop(15)
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(total);
    }

    private void agregarHeaderCell(Table table, String texto) {
        Cell header = new Cell()
                .add(new Paragraph(texto)
                    .setFontSize(10)
                    .setBold()
                    .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(COLOR_PRIMARY)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(8);
        table.addHeaderCell(header);
    }

    private void agregarDataCell(Table table, String texto, TextAlignment alignment) {
        Cell cell = new Cell()
                .add(new Paragraph(texto).setFontSize(9))
                .setTextAlignment(alignment)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(6)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
        table.addCell(cell);
    }

    private DeviceRgb obtenerColorEstado(String estado) {
        return switch (estado) {
            case "CONFIRMADA" -> COLOR_SUCCESS;
            case "PENDIENTE" -> COLOR_WARNING;
            case "CANCELADA" -> COLOR_DANGER;
            case "COMPLETADA" -> COLOR_SECONDARY;
            case "VENCIDA" -> new DeviceRgb(220, 53, 69);
            default -> COLOR_SECONDARY;
        };
    }
}
