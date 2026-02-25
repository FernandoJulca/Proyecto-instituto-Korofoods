package com.koroFoods.menuService.service;

import com.koroFoods.menuService.dto.EtiquetaDtoFeign;
import com.koroFoods.menuService.dto.PlatoDtoFeign;
import com.koroFoods.menuService.dto.ReportePlatoItem;
import com.koroFoods.menuService.dto.ResultadoResponse;
import com.koroFoods.menuService.dto.request.IncrementarStock;
import com.koroFoods.menuService.dto.request.PlatoStockDto;
import com.koroFoods.menuService.model.Plato;
import com.koroFoods.menuService.repository.IMenuRepository;
import com.koroFoods.menuService.repository.IPlatoEtiquetaRepository;
import com.koroFoods.menuService.repository.IPlatoRepository;

import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {

	private final IMenuRepository menuRepository;

	@Autowired
	private PdfPlatosService pdfPlatosService;
	
	@Autowired
	private IPlatoRepository platoRepository;
	
	@Autowired
	private IPlatoEtiquetaRepository platoEtiquetaRepository;
	// Método para restar el stock de los pedidos consumidos
	public ResultadoResponse<PlatoDtoFeign> substractStockOrder(Integer idPlato, Integer cantidadVendida) {
        Plato plato = menuRepository.findById(idPlato)
                .orElseThrow(() -> new RuntimeException("El plato no existe"));
        if (plato.getStock() < cantidadVendida) {
            return ResultadoResponse.error("Stock insuficiente para el plato: " + plato.getNombre());
        }
        plato.setStock(plato.getStock() - cantidadVendida);
        menuRepository.save(plato);
        PlatoDtoFeign dto = new PlatoDtoFeign();
        dto.setIdPlato(plato.getIdPlato());
        dto.setNombre(plato.getNombre());
        dto.setPrecio(plato.getPrecio());
        dto.setStock(plato.getStock());

        return ResultadoResponse.success("Stock actualizado", dto);
    }
    
	// Método para el feign de la reseña
	public ResultadoResponse<List<PlatoDtoFeign>> getAllDish() {
	    List<Plato> platos = menuRepository.findActivosConEtiquetasOrdenados();

	    List<PlatoDtoFeign> dtos = platos.stream().map(plato -> {
	        PlatoDtoFeign dto = new PlatoDtoFeign();
	        dto.setIdPlato(plato.getIdPlato());
	        dto.setNombre(plato.getNombre());
	        dto.setTipoPlato(plato.getTipoPlato().toString());
	        dto.setImagen(plato.getImagen());
	        dto.setStock(plato.getStock());
	        dto.setPrecio(plato.getPrecio());

	        // 👇 Mapea etiquetas activas
	        List<EtiquetaDtoFeign> etiquetas = plato.getPlatoEtiquetas().stream()
	            .filter(pe -> pe.getActivo() && pe.getEtiqueta().getActivo())
	            .map(pe -> {
	                EtiquetaDtoFeign e = new EtiquetaDtoFeign();
	                e.setIdEtiqueta(pe.getEtiqueta().getIdEtiqueta());
	                e.setNombre(pe.getEtiqueta().getNombre());
	                return e;
	            }).toList();

	        dto.setEtiquetas(etiquetas);
	        return dto;
	    }).toList();

	    return ResultadoResponse.success("Platos encontrados", dtos);
	}

	public ResultadoResponse<PlatoDtoFeign> getDishById(Integer id) {
		Plato dish = menuRepository.findById(id).orElseThrow(() -> new RuntimeException("Plato no encontrado"));

		PlatoDtoFeign dto = new PlatoDtoFeign();
		dto.setIdPlato(dish.getIdPlato());
		dto.setNombre(dish.getNombre());
		dto.setTipoPlato(dish.getTipoPlato().toString());
		dto.setImagen(dish.getImagen());
        dto.setStock(dish.getStock());
		dto.setPrecio(dish.getPrecio());
		return ResultadoResponse.success("Plato encontrado", dto);
	}

	public byte[] generateMenuPdf(List<PlatoDtoFeign> platos) throws Exception {
	    Document document = new Document(PageSize.A4);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();

	    try {
	        PdfWriter writer = PdfWriter.getInstance(document, baos);

	        writer.setPageEvent(new PdfPageEventHelper() {
	            @Override
	            public void onEndPage(PdfWriter writer, Document document) {
	                try {
	                    PdfPTable footer = new PdfPTable(1);
	                    footer.setTotalWidth(document.getPageSize().getWidth() - 80);
	                    footer.setLockedWidth(true);

	                    Font footerFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC,
	                            new BaseColor(128, 128, 128));
	                    PdfPCell cell = new PdfPCell(
	                            new Phrase("© 2025 KoroFood - Experiencias Gastronómicas Únicas | Página "
	                                    + writer.getPageNumber(), footerFont));
	                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	                    cell.setBorder(Rectangle.NO_BORDER);
	                    cell.setPaddingTop(10);
	                    footer.addCell(cell);

	                    footer.writeSelectedRows(0, -1, 40, 40, writer.getDirectContent());
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        });

	        document.open();

	        BaseColor primaryOrange = new BaseColor(230, 126, 34);
	        BaseColor darkBrown = new BaseColor(62, 39, 35);
	        BaseColor lightBeige = new BaseColor(250, 249, 246);
	        BaseColor textGray = new BaseColor(107, 107, 107);
	        BaseColor priceGreen = new BaseColor(39, 174, 96);

	        PdfPTable headerTable = new PdfPTable(2);
	        headerTable.setWidthPercentage(100);
	        headerTable.setWidths(new float[] { 3, 2 });
	        headerTable.setSpacingAfter(20);

	        PdfPCell logoCell = new PdfPCell();
	        logoCell.setBorder(Rectangle.NO_BORDER);
	        logoCell.setPaddingBottom(10);

	        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 32, Font.BOLD, primaryOrange);
	        Paragraph title = new Paragraph("KoroFood", titleFont);
	        title.setAlignment(Element.ALIGN_LEFT);
	        logoCell.addElement(title);

	        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, textGray);
	        Paragraph subtitle = new Paragraph("Menú de Platos Especiales", subtitleFont);
	        subtitle.setSpacingBefore(5);
	        logoCell.addElement(subtitle);

	        headerTable.addCell(logoCell);

	        PdfPCell dateCell = new PdfPCell();
	        dateCell.setBorder(Rectangle.NO_BORDER);
	        dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        dateCell.setVerticalAlignment(Element.ALIGN_TOP);

	        Font dateFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, textGray);
	        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	        Paragraph datePara = new Paragraph("Fecha: " + currentDate, dateFont);
	        datePara.setAlignment(Element.ALIGN_RIGHT);
	        dateCell.addElement(datePara);

	        Paragraph totalPlatos = new Paragraph("Total de platos: " + platos.size(), dateFont);
	        totalPlatos.setAlignment(Element.ALIGN_RIGHT);
	        totalPlatos.setSpacingBefore(3);
	        dateCell.addElement(totalPlatos);

	        headerTable.addCell(dateCell);
	        document.add(headerTable);

	        LineSeparator line = new LineSeparator();
	        line.setLineColor(primaryOrange);
	        line.setLineWidth(2);
	        document.add(new Chunk(line));
	        document.add(Chunk.NEWLINE);

	        Font introFont = new Font(Font.FontFamily.HELVETICA, 11, Font.ITALIC, textGray);
	        Paragraph intro = new Paragraph("Descubre nuestra exquisita selección de platos asiáticos, "
	                + "cuidadosamente preparados con ingredientes frescos y auténticos. "
	                + "Cada plato es una experiencia única que combina tradición y sabor.", introFont);
	        intro.setAlignment(Element.ALIGN_JUSTIFIED);
	        intro.setSpacingAfter(20);
	        document.add(intro);

	        Map<String, List<PlatoDtoFeign>> platosPorTipo = platos.stream()
	                .sorted(Comparator.comparingInt(p -> ordenTipo(p.getTipoPlato()))).collect(Collectors
	                        .groupingBy(PlatoDtoFeign::getTipoPlato, LinkedHashMap::new, Collectors.toList()));

	        // Calcular estadísticas por categoría
	        for (Map.Entry<String, List<PlatoDtoFeign>> entry : platosPorTipo.entrySet()) {

	            String tipoPlato = traducirTipoPlato(entry.getKey());
	            List<PlatoDtoFeign> platosDelTipo = entry.getValue();

	            // Calcular precio promedio de la categoría
	            BigDecimal suma = platosDelTipo.stream()
	                    .map(PlatoDtoFeign::getPrecio)
	                    .reduce(BigDecimal.ZERO, BigDecimal::add);

	            BigDecimal promedio = BigDecimal.ZERO;

	            if (!platosDelTipo.isEmpty()) {
	                promedio = suma.divide(
	                        BigDecimal.valueOf(platosDelTipo.size()),
	                        2, 
	                        RoundingMode.HALF_UP
	                );
	            }


	            Font categoryFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, darkBrown);
	            Paragraph categoryTitle = new Paragraph(tipoPlato.toUpperCase(), categoryFont);
	            categoryTitle.setSpacingBefore(15);
	            categoryTitle.setSpacingAfter(5);

	            PdfPTable categoryHeader = new PdfPTable(2);
	            categoryHeader.setWidthPercentage(100);
	            categoryHeader.setWidths(new float[] { 3f, 1f });
	            
	            PdfPCell categoryCell = new PdfPCell(categoryTitle);
	            categoryCell.setBackgroundColor(lightBeige);
	            categoryCell.setBorder(Rectangle.NO_BORDER);
	            categoryCell.setPadding(10);
	            categoryCell.setBorderWidthLeft(4);
	            categoryCell.setBorderColorLeft(primaryOrange);
	            categoryHeader.addCell(categoryCell);

	            // Celda de estadísticas
	            PdfPCell statsCell = new PdfPCell();
	            statsCell.setBackgroundColor(lightBeige);
	            statsCell.setBorder(Rectangle.NO_BORDER);
	            statsCell.setPadding(10);
	            statsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	            
	            Font statsFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, textGray);
	            Phrase statsPhrase = new Phrase();
	            statsPhrase.add(new Chunk(platosDelTipo.size() + " platos | ", statsFont));
	            statsPhrase.add(new Chunk("Precio promedio: S/ " + String.format("%.2f", promedio), 
	                    new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, priceGreen)));
	            statsCell.addElement(statsPhrase);
	            
	            categoryHeader.addCell(statsCell);

	            document.add(categoryHeader);
	            document.add(Chunk.NEWLINE);

	            // Tabla de platos con 4 columnas
	            PdfPTable table = new PdfPTable(4);
	            table.setWidthPercentage(100);
	            table.setWidths(new float[] { 3f, 1.5f, 1.2f, 1.8f }); 
	            table.setSpacingAfter(20);

	            Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
	            String[] headers = { "Plato", "Imagen", "Precio", "Etiquetas" };

	            for (String header : headers) {
	                PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
	                headerCell.setBackgroundColor(primaryOrange);
	                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	                headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
	                headerCell.setPadding(8);
	                headerCell.setBorder(Rectangle.NO_BORDER);
	                table.addCell(headerCell);
	            }

	            Font nameFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, darkBrown);
	            Font priceFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, priceGreen);
	            Font tagFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.WHITE);

	            int contador = 0;
	            for (PlatoDtoFeign plato : platosDelTipo) {
	                BaseColor rowColor = (contador % 2 == 0) ? BaseColor.WHITE : new BaseColor(250, 250, 250);

	                // Nombre
	                PdfPCell nameCell = new PdfPCell(new Phrase(plato.getNombre(), nameFont));
	                nameCell.setBackgroundColor(rowColor);
	                nameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
	                nameCell.setPadding(12);
	                nameCell.setBorder(Rectangle.NO_BORDER);
	                table.addCell(nameCell);

	                // Imagen
	                PdfPCell imageCell = new PdfPCell();
	                imageCell.setBackgroundColor(rowColor);
	                imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	                imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
	                imageCell.setPadding(5);
	                imageCell.setBorder(Rectangle.NO_BORDER);

	                try {
	                    if (plato.getImagen() != null && !plato.getImagen().isEmpty()) {
	                        URL url = new URL(plato.getImagen());
	                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	                        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
	                        connection.connect();

	                        try (InputStream in = connection.getInputStream()) {
	                            BufferedImage original = ImageIO.read(in);

	                            if (original != null) {
	                                int min = Math.min(original.getWidth(), original.getHeight());
	                                int x = (original.getWidth() - min) / 2;
	                                int y = (original.getHeight() - min) / 2;

	                                BufferedImage square = original.getSubimage(x, y, min, min);
	                                BufferedImage resized = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
	                                Graphics2D g = resized.createGraphics();

	                                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	                                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	                                g.setRenderingHint(RenderingHints.KEY_RENDERING,
	                                        RenderingHints.VALUE_RENDER_QUALITY);
	                                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                                        RenderingHints.VALUE_ANTIALIAS_ON);
	                                g.drawImage(square, 0, 0, 200, 200, null);
	                                g.dispose();

	                                ByteArrayOutputStream baosx = new ByteArrayOutputStream();
	                                ImageIO.write(resized, "png", baosx);
	                                byte[] bytes = baosx.toByteArray();

	                                Image img = Image.getInstance(bytes);
	                                img.scaleToFit(80f, 80f);
	                                img.setAlignment(Image.ALIGN_CENTER);
	                                img.setBorder(Rectangle.BOX);
	                                img.setBorderWidth(2);
	                                img.setBorderColor(primaryOrange);

	                                imageCell.addElement(img);
	                            } else {
	                                imageCell.addElement(new Phrase("Sin imagen"));
	                            }
	                        }
	                    } else {
	                        Font noImgFont = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY);
	                        imageCell.addElement(new Phrase("Sin imagen", noImgFont));
	                    }
	                } catch (Exception e) {
	                    Font errorFont = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.RED);
	                    imageCell.addElement(new Phrase("Error", errorFont));
	                }

	                table.addCell(imageCell);

	                // Precio
	                PdfPCell priceCell = new PdfPCell(new Phrase("S/ " + String.format("%.2f", plato.getPrecio()), priceFont));
	                priceCell.setBackgroundColor(rowColor);
	                priceCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	                priceCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
	                priceCell.setPadding(12);
	                priceCell.setBorder(Rectangle.NO_BORDER);
	                table.addCell(priceCell);

	                // Etiquetas
	                PdfPCell tagsCell = new PdfPCell();
	                tagsCell.setBackgroundColor(rowColor);
	                tagsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
	                tagsCell.setPadding(8);
	                tagsCell.setBorder(Rectangle.NO_BORDER);

	                if (plato.getEtiquetas() != null && !plato.getEtiquetas().isEmpty()) {
	                    PdfPTable tagsTable = new PdfPTable(1);
	                    tagsTable.setWidthPercentage(100);
	                    
	                    for (EtiquetaDtoFeign etiqueta : plato.getEtiquetas()) {
	                        PdfPCell tagCell = new PdfPCell(new Phrase(etiqueta.getNombre(), tagFont));
	                        tagCell.setBackgroundColor(primaryOrange);
	                        tagCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	                        tagCell.setPadding(4);
	                        tagCell.setBorder(Rectangle.NO_BORDER);
	                        tagCell.setBorder(3);
	                        tagsTable.addCell(tagCell);
	                    }
	                    tagsCell.addElement(tagsTable);
	                } else {
	                    Font noTagFont = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.LIGHT_GRAY);
	                    tagsCell.addElement(new Phrase("-", noTagFont));
	                }

	                table.addCell(tagsCell);
	                contador++;
	            }
	            document.add(table);
	        }

	        // Resumen final
	        document.add(Chunk.NEWLINE);
	        
	        PdfPTable summaryTable = new PdfPTable(3);
	        summaryTable.setWidthPercentage(100);
	        summaryTable.setSpacingBefore(10);
	        summaryTable.setSpacingAfter(15);

	        Font summaryLabelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, darkBrown);
	        Font summaryValueFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, priceGreen);

	        // Total de platos
	        PdfPCell totalPlatosCell = new PdfPCell();
	        totalPlatosCell.setBackgroundColor(lightBeige);
	        totalPlatosCell.setPadding(10);
	        totalPlatosCell.setBorder(Rectangle.NO_BORDER);
	        totalPlatosCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        totalPlatosCell.addElement(new Phrase("Total de Platos", summaryLabelFont));
	        totalPlatosCell.addElement(new Phrase(String.valueOf(platos.size()), summaryValueFont));
	        summaryTable.addCell(totalPlatosCell);

	        // Precio mínimo
	        BigDecimal precioMin = platos.stream()
	                .map(PlatoDtoFeign::getPrecio)
	                .min(BigDecimal::compareTo)
	                .orElse(BigDecimal.ZERO);
	        PdfPCell minPriceCell = new PdfPCell();
	        minPriceCell.setBackgroundColor(lightBeige);
	        minPriceCell.setPadding(10);
	        minPriceCell.setBorder(Rectangle.NO_BORDER);
	        minPriceCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        minPriceCell.addElement(new Phrase("Desde", summaryLabelFont));
	        minPriceCell.addElement(new Phrase("S/ " + String.format("%.2f", precioMin), summaryValueFont));
	        summaryTable.addCell(minPriceCell);

	        // Precio máximo
	        BigDecimal precioMax = platos.stream()
	                .map(PlatoDtoFeign::getPrecio)
	                .max(BigDecimal::compareTo)
	                .orElse(BigDecimal.ZERO);
	        PdfPCell maxPriceCell = new PdfPCell();
	        maxPriceCell.setBackgroundColor(lightBeige);
	        maxPriceCell.setPadding(10);
	        maxPriceCell.setBorder(Rectangle.NO_BORDER);
	        maxPriceCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        maxPriceCell.addElement(new Phrase("Hasta", summaryLabelFont));
	        maxPriceCell.addElement(new Phrase("S/ " + String.format("%.2f", precioMax), summaryValueFont));
	        summaryTable.addCell(maxPriceCell);

	        document.add(summaryTable);

	        LineSeparator bottomLine = new LineSeparator();
	        bottomLine.setLineColor(primaryOrange);
	        bottomLine.setLineWidth(1);
	        document.add(new Chunk(bottomLine));

	        PdfPTable infoTable = new PdfPTable(3);
	        infoTable.setWidthPercentage(100);
	        infoTable.setSpacingBefore(15);
	        infoTable.setWidths(new float[] { 1f, 1f, 1f });

	        Font infoTitleFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, darkBrown);
	        Font infoDataFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, textGray);

	        // Información de contacto
	        PdfPCell contactCell = new PdfPCell();
	        contactCell.setBorder(Rectangle.NO_BORDER);
	        contactCell.addElement(new Phrase("Contacto", infoTitleFont));
	        contactCell.addElement(new Phrase("Av. Larco 123, Miraflores, Lima", infoDataFont));
	        contactCell.addElement(new Phrase("+51 987 654 321", infoDataFont));
	        contactCell.addElement(new Phrase("hola@korofood.pe", infoDataFont));
	        infoTable.addCell(contactCell);

	        // Horarios
	        PdfPCell horariosCell = new PdfPCell();
	        horariosCell.setBorder(Rectangle.NO_BORDER);
	        horariosCell.addElement(new Phrase("Horarios de Atención", infoTitleFont));
	        horariosCell.addElement(new Phrase("Lun - Jue: 12:00 - 22:00", infoDataFont));
	        horariosCell.addElement(new Phrase("Vie - Sáb: 12:00 - 24:00", infoDataFont));
	        horariosCell.addElement(new Phrase("Dom: 12:00 - 21:00", infoDataFont));
	        infoTable.addCell(horariosCell);

	        // Nota importante
	        PdfPCell notaCell = new PdfPCell();
	        notaCell.setBorder(Rectangle.NO_BORDER);
	        notaCell.addElement(new Phrase("Nota Importante", infoTitleFont));
	        notaCell.addElement(new Phrase("Los precios incluyen IGV", infoDataFont));
	        notaCell.addElement(new Phrase("Carta sujeta a cambios", infoDataFont));
	        infoTable.addCell(notaCell);

	        document.add(infoTable);

	    } finally {
	        document.close();
	    }

	    return baos.toByteArray();
	}

	private String traducirTipoPlato(String tipo) {
		switch (tipo) {
		case "E":
			return "Entrada";
		case "S":
			return "Segundo";
		case "P":
			return "Postre";
		case "B":
			return "Bebida";
		default:
			return tipo;
		}
	}

	private int ordenTipo(String tipo) {
		switch (tipo) {
		case "E":
			return 1;
		case "S":
			return 2;
		case "P":
			return 3;
		case "B":
			return 4;
		default:
			return 99;
		}
	}


    public ResultadoResponse<PlatoStockDto> aumentarStock(IncrementarStock request){

        PlatoStockDto response = new PlatoStockDto();

        validarId(request.getIdPlato());

        if (request.getCantidad() <= 0){

            throw  new RuntimeException("Tienes que poner una cantidad a devolver");
        }

        Plato actualizar = obtenerPlato(request.getIdPlato());

        Integer aumentar = actualizar.getStock() + request.getCantidad();
        actualizar.setStock(aumentar);
        menuRepository.save(actualizar);

        response.setIdPlato(actualizar.getIdPlato());
        response.setNombre(actualizar.getNombre());
        response.setCantidad(actualizar.getStock());

        return ResultadoResponse.success("Se actualizo el stock: ", response );

    }


    private Plato obtenerPlato(Integer idPlato){
        validarId(idPlato);

        return menuRepository.findById(idPlato)
                .orElseThrow(() -> new RuntimeException("No se pudo obtener el plato con ID: " + idPlato));
    }


    private void validarId(Integer request){
        if (request == null || request <= 0) {
            log.error("Error al obtener con ID: {}", request);
            throw new IllegalArgumentException("ID invalido");
        }
    }
    
    public byte[] generarReportePlatos() {
        List<Plato> platos = platoRepository.findAll();

        List<ReportePlatoItem> items = platos.stream().map(p -> {
            ReportePlatoItem item = new ReportePlatoItem();
            item.setIdPlato(p.getIdPlato());
            item.setNombre(p.getNombre());
            item.setPrecio(p.getPrecio());
            item.setStock(p.getStock());
            item.setTipoPlato(p.getTipoPlato().name());
            item.setTipoPlatoDescripcion(switch (p.getTipoPlato().name()) {
                case "E" -> "Entrada";
                case "S" -> "Sopa";
                case "P" -> "Plato Principal";
                case "B" -> "Bebida";
                default  -> p.getTipoPlato().name();
            });
            item.setActivo(p.getActivo());

            // Obtener etiquetas activas
            List<String> etiquetas = platoEtiquetaRepository
                    .findByPlato_IdPlatoAndActivo(p.getIdPlato(), true)
                    .stream()
                    .map(pe -> pe.getEtiqueta().getNombre())
                    .toList();
            item.setEtiquetas(etiquetas);

            return item;
        }).toList();

        return pdfPlatosService.generarReportePlatos(items);
    }
}
