package com.koroFoods.paymentService.service;


import com.koroFoods.paymentService.dtos.response.GraficoTresData;
import com.koroFoods.paymentService.dtos.response.ResultadoResponse;
import com.koroFoods.paymentService.util.DashboardNotificator;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.koroFoods.paymentService.dtos.ConfirmarPagoRequest;
import com.koroFoods.paymentService.dtos.CrearPagoRequest;
import com.koroFoods.paymentService.dtos.PagoAnuladoEvent;
import com.koroFoods.paymentService.dtos.PagoConfirmadoEvent;
import com.koroFoods.paymentService.dtos.PagoResponse;
import com.koroFoods.paymentService.dtos.QRDataResponse;
import com.koroFoods.paymentService.dtos.ReporteIngresoItem;
import com.koroFoods.paymentService.dtos.SubirCapturaRequest;
import com.koroFoods.paymentService.enums.EstadoPago;
import com.koroFoods.paymentService.enums.MetodoPago;
import com.koroFoods.paymentService.enums.MotivoRechazo;
import com.koroFoods.paymentService.enums.TipoPago;
import com.koroFoods.paymentService.exception.BusinessException;
import com.koroFoods.paymentService.exception.ResourceNotFoundException;
import com.koroFoods.paymentService.messaging.PagoEventPublisher;
import com.koroFoods.paymentService.model.Pago;
import com.koroFoods.paymentService.repository.IPagoRepository;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoService {

	private final IPagoRepository pagoRepository;
    private final PagoEventPublisher eventPublisher;

    private final DashboardNotificator dashboardNotificator;
    
    @Autowired
    private PdfIngresosService pdfIngresosService;
    
    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private GoogleVisionService googleVisionService;

    @Autowired 
    private OCRValidationService ocrValidationService;

    // Datos del negocio para Yape/Plin
    private static final String NUMERO_YAPE = "986425458"; // Número de Yape del negocio
    private static final String NUMERO_PLIN = "986425458"; // Número de Plin del negocio
    private static final String NOMBRE_NEGOCIO = "KoroFood Restaurant";

    @Transactional
    public QRDataResponse crearPago(CrearPagoRequest request) {
        validarRequest(request);

        Pago pago = new Pago();
        pago.setIdReserva(request.getIdReserva());
        pago.setIdPedido(request.getIdPedido());
        pago.setIdUsuario(request.getIdUsuario());
        pago.setTipoPago(TipoPago.valueOf(request.getTipoPago()));
        pago.setMonto(request.getMonto());
        pago.setMetodoPago(request.getMetodoPago());
        pago.setObservaciones(request.getObservaciones());

        if (request.getMetodoPago() == MetodoPago.EFECTIVO || 
            request.getMetodoPago() == MetodoPago.TARJETA) {
            pago.setEstado(EstadoPago.PAG);
            pago.setFechaPago(LocalDateTime.now());
        } else {
            pago.setEstado(EstadoPago.PEN);
        }

        pago.setReferenciaPago(generarReferencia());
        Pago guardado = pagoRepository.save(pago);

        if (guardado.getEstado() == EstadoPago.PAG) {
            publicarEventoPagoConfirmado(guardado);
        }

        dashboardNotificator.notificarGraficoTres(LocalDate.now().getMonthValue());

        return generarQRData(guardado);
    }

    @Transactional
    public PagoResponse confirmarPago(ConfirmarPagoRequest request) {
        Pago pago = pagoRepository.findById(request.getIdPago())
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con ID: " + request.getIdPago()));

        // Validar estado
        if (pago.getEstado() != EstadoPago.PEN) {
            throw new BusinessException("El pago ya fue procesado. Estado actual: " + pago.getEstado());
        }

        // Validar expiración
        if (LocalDateTime.now().isAfter(pago.getFechaExpiracion())) {
            pago.setEstado(EstadoPago.EXP);
            pagoRepository.save(pago);
            throw new BusinessException("El pago ha expirado. Por favor, genere uno nuevo.");
        }

        // Validar código de operación único
        if (pagoRepository.existsByCodigoOperacion(request.getCodigoOperacion())) {
            throw new BusinessException("El código de operación ya fue utilizado");
        }

        // Confirmar pago
        pago.setCodigoOperacion(request.getCodigoOperacion());
        pago.setEstado(EstadoPago.PAG);
        pago.setFechaPago(LocalDateTime.now());
        if (request.getObservaciones() != null) {
            pago.setObservaciones(request.getObservaciones());
        }

        Pago confirmado = pagoRepository.save(pago);

        // Publicar evento en RabbitMQ
        publicarEventoPagoConfirmado(confirmado);



        return mapearAResponse(confirmado);
    }

    @Transactional
    public PagoResponse anularPago(Integer idPago, String motivo) {
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con ID: " + idPago));

        if (pago.getEstado() == EstadoPago.PAG) {
            throw new BusinessException("No se puede anular un pago ya confirmado");
        }

        pago.setEstado(EstadoPago.ANU);
        pago.setObservaciones(motivo);

        Pago anulado = pagoRepository.save(pago);

        // Publicar evento en RabbitMQ
        publicarEventoPagoAnulado(anulado, motivo);

        return mapearAResponse(anulado);
    }

    @Transactional(readOnly = true)
    public List<PagoResponse> listarTodos() {
        return pagoRepository.findAll().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PagoResponse> listarPorUsuario(Integer idUsuario) {
        return pagoRepository.findByIdUsuario(idUsuario).stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PagoResponse buscarPorId(Integer id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con ID: " + id));
        return mapearAResponse(pago);
    }

    @Transactional(readOnly = true)
    public PagoResponse buscarPorReferencia(String referencia) {
        Pago pago = pagoRepository.findByReferenciaPago(referencia)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con referencia: " + referencia));
        return mapearAResponse(pago);
    }

    // Métodos privados

    private void validarRequest(CrearPagoRequest request) {
        if (request.getIdReserva() == null && request.getIdPedido() == null) {
            throw new BusinessException("Debe especificar ID de reserva o ID de pedido");
        }

        if (request.getIdReserva() != null && request.getIdPedido() != null) {
            throw new BusinessException("Solo puede especificar ID de reserva O ID de pedido, no ambos");
        }

        // Validar método de pago según tipo
        MetodoPago metodo = (request.getMetodoPago());
        if (request.getTipoPago().equals("DR") && metodo == MetodoPago.EFECTIVO) {
            throw new BusinessException("No se acepta efectivo para depósitos de reserva");
        }
    }

    private String generarReferencia() {
        return "KORO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

   private QRDataResponse generarQRData(Pago pago) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    if (pago.getMetodoPago() == MetodoPago.EFECTIVO || 
        pago.getMetodoPago() == MetodoPago.TARJETA) {
        return QRDataResponse.builder()
                .idPago(pago.getIdPago())
                .referenciaPago(pago.getReferenciaPago())
                .monto(pago.getMonto())
                .metodoPago(pago.getMetodoPago())
                .numeroDestino(null)
                .nombreDestino(NOMBRE_NEGOCIO)
                .concepto("Pago en " + obtenerDescripcionMetodoPago(pago.getMetodoPago()))
                .qrData(null) 
                .fechaExpiracion(pago.getFechaExpiracion() != null 
                    ? pago.getFechaExpiracion().format(formatter) : null)
                .build();
    }

    // YAPE / PLIN → generan QR como antes
    String numeroDestino;
    if (pago.getMetodoPago() == MetodoPago.YAPE) {
        numeroDestino = NUMERO_YAPE;
    } else {
        numeroDestino = NUMERO_PLIN;
    }

    String concepto = String.format("KoroFood - Ref: %s", pago.getReferenciaPago());
    String qrData = String.format(
            "%s://pago?numero=%s&monto=%.2f&concepto=%s",
            pago.getMetodoPago().name().toLowerCase(),
            numeroDestino,
            pago.getMonto(),
            concepto
    );

    return QRDataResponse.builder()
            .idPago(pago.getIdPago())
            .referenciaPago(pago.getReferenciaPago())
            .monto(pago.getMonto())
            .metodoPago(pago.getMetodoPago())
            .numeroDestino(numeroDestino)
            .nombreDestino(NOMBRE_NEGOCIO)
            .concepto(concepto)
            .qrData(qrData)
            .fechaExpiracion(pago.getFechaExpiracion().format(formatter))
            .build();
}

    private void publicarEventoPagoConfirmado(Pago pago) {
        PagoConfirmadoEvent event = PagoConfirmadoEvent.builder()
                .idPago(pago.getIdPago())
                .idReserva(pago.getIdReserva())
                .idPedido(pago.getIdPedido())
                .idUsuario(pago.getIdUsuario())
                .tipoPago(pago.getTipoPago().name())
                .monto(pago.getMonto())
                .metodoPago(pago.getMetodoPago())
                .fechaPago(pago.getFechaPago())
                .codigoOperacion(pago.getCodigoOperacion())
                .build();

        eventPublisher.publicarPagoConfirmado(event);
    }

    private void publicarEventoPagoAnulado(Pago pago, String motivo) {
        PagoAnuladoEvent event = PagoAnuladoEvent.builder()
                .idPago(pago.getIdPago())
                .idReserva(pago.getIdReserva())
                .idPedido(pago.getIdPedido())
                .motivo(motivo)
                .build();

        eventPublisher.publicarPagoAnulado(event);
    }

    private String obtenerDescripcionTipoPago(TipoPago tipo) {
        return switch (tipo) {
            case DR -> "Depósito Reserva";
            case PP -> "Pago Pedido";
        };
    }

    private String obtenerDescripcionMetodoPago(MetodoPago metodo) {
        return switch (metodo) {
            case YAPE -> "Yape";
            case PLIN -> "Plin";
            case EFECTIVO -> "Efectivo";
            case TARJETA -> "Tarjeta";
        };
    }

    private String obtenerDescripcionEstado(EstadoPago estado) {
        return switch (estado) {
            case PEN -> "Pendiente";
            case PAG -> "Pagado";
            case ANU -> "Anulado";
            case EXP -> "Expirado";
            case RECH -> "Rechazado";
        };
    }

   
    
    @Transactional
    public PagoResponse subirCaptura(SubirCapturaRequest request) {
        // 1. Buscar el pago
        Pago pago = pagoRepository.findById(request.getIdPago())
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con ID: " + request.getIdPago()));

        // 2. Validar que esté en estado PENDIENTE
        if (pago.getEstado() != EstadoPago.PEN) {
            throw new BusinessException("El pago ya fue procesado. Estado actual: " + pago.getEstado());
        }

        // 3. Validar que no haya expirado
        if (pago.getFechaExpiracion() != null && LocalDateTime.now().isAfter(pago.getFechaExpiracion())) {
            pago.setEstado(EstadoPago.EXP);
            pagoRepository.save(pago);
            throw new BusinessException("El pago ha expirado. Por favor, crea una nueva reserva.");
        }

        // 4. Validar que el método de pago coincida
        if (!pago.getMetodoPago().name().equals(request.getMetodoPago())) {
            throw new BusinessException(
                String.format("Método de pago incorrecto. Esperado: %s, Recibido: %s", 
                    pago.getMetodoPago(), request.getMetodoPago())
            );
        }

        try {
            // 5. Generar hash SHA256 de la imagen
            String hash = generarHashImagen(request.getImagenBase64());
            
            // 6. Verificar que el hash NO exista (imagen no duplicada)
            if (pagoRepository.existsByHashImagen(hash)) {
                pago.setEstado(EstadoPago.RECH);
                pago.setMotivoRechazo(MotivoRechazo.HASH_DUPLICADO.getMensaje());
                pagoRepository.save(pago);
                
                throw new BusinessException(MotivoRechazo.HASH_DUPLICADO.getMensaje());
            }
            
            pago.setHashImagen(hash);
            
            // 7. Subir imagen a Cloudinary
            log.info("Subiendo imagen a Cloudinary para pago: {}", pago.getReferenciaPago());
            String urlCaptura = cloudinaryService.subirImagenBase64(
                request.getImagenBase64(), 
                pago.getReferenciaPago()
            );
            pago.setUrlCaptura(urlCaptura);
            log.info("Imagen subida exitosamente: {}", urlCaptura);
            
            // 8. Extraer texto con Google Vision OCR
            log.info("Extrayendo texto con Google Vision...");
            String textoExtraido = googleVisionService.extraerTextoDeURL(urlCaptura);
            pago.setTextoExtraido(textoExtraido);
            log.info("Texto extraído ({} caracteres)", textoExtraido.length());
            
            // 9. Validar con OCR
            OCRValidationService.ResultadoOCR resultadoOCR = ocrValidationService.validarTextoOCR(
                textoExtraido, 
                pago.getMonto(), 
                pago.getMetodoPago()
            );
            
            // 10. Guardar datos extraídos
            pago.setCodigoOperacion(resultadoOCR.getCodigoOperacion());
            pago.setMontoDetectado(resultadoOCR.getMontoDetectado());
            pago.setFechaDetectada(resultadoOCR.getFechaDetectada());
            
            // 11. Validar resultado
            if (!resultadoOCR.isValido()) {
                // Validación falló
                pago.setEstado(EstadoPago.RECH);
                pago.setMotivoRechazo(resultadoOCR.getMotivoRechazo().getMensaje());
                
                Pago rechazado = pagoRepository.save(pago);
                
                log.warn("Pago rechazado: {}", resultadoOCR.getMotivoRechazo());
                
                throw new BusinessException(resultadoOCR.getMotivoRechazo().getMensaje());
            }
            
            // 12. Verificar que el código de operación NO exista
            if (pagoRepository.existsByCodigoOperacion(pago.getCodigoOperacion())) {
                pago.setEstado(EstadoPago.RECH);
                pago.setMotivoRechazo(MotivoRechazo.CODIGO_DUPLICADO.getMensaje());
                pagoRepository.save(pago);
                
                throw new BusinessException(MotivoRechazo.CODIGO_DUPLICADO.getMensaje());
            }
            
            // 13. ✅ VALIDACIÓN EXITOSA
            pago.setEstado(EstadoPago.PAG);
            pago.setFechaPago(LocalDateTime.now());
            
            Pago pagado = pagoRepository.save(pago);
            
            log.info(" Pago validado exitosamente: {}", pago.getReferenciaPago());
            
            // 14. Publicar evento en RabbitMQ
            publicarEventoPagoConfirmado(pagado);
            
            return mapearAResponse(pagado);
            
        } catch (BusinessException e) {
            // Re-lanzar excepciones de negocio
            throw e;
        } catch (Exception e) {
            // Error técnico (Cloudinary, Google Vision, etc.)
            log.error("Error técnico al procesar captura", e);
            
            pago.setEstado(EstadoPago.RECH);
            pago.setMotivoRechazo(MotivoRechazo.ERROR_OCR.getMensaje() + ": " + e.getMessage());
            pagoRepository.save(pago);
            
            throw new BusinessException("Error al procesar la captura: " + e.getMessage());
        }
    }

    private String generarHashImagen(String base64Image) {
        try {
            // Limpiar el prefijo data:image/... si existe
            String base64Data = base64Image;
            if (base64Image.contains(",")) {
                base64Data = base64Image.split(",")[1];
            }
            
            // Decodificar Base64
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            
            // Generar hash SHA256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(imageBytes);
            
            // Convertir a hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (Exception e) {
            throw new RuntimeException("Error al generar hash de imagen", e);
        }
    }

    // Actualizar el método mapearAResponse para incluir nuevos campos
    private PagoResponse mapearAResponse(Pago pago) {
        return PagoResponse.builder()
                .idPago(pago.getIdPago())
                .idReserva(pago.getIdReserva())
                .idPedido(pago.getIdPedido())
                .idUsuario(pago.getIdUsuario())
                .tipoPago(pago.getTipoPago().name())
                .tipoPagoDescripcion(obtenerDescripcionTipoPago(pago.getTipoPago()))
                .monto(pago.getMonto())
                .metodoPago(pago.getMetodoPago().name())
                .metodoPagoDescripcion(obtenerDescripcionMetodoPago(pago.getMetodoPago()))
                .fechaPago(pago.getFechaPago())
                .estado(pago.getEstado().name())
                .estadoDescripcion(obtenerDescripcionEstado(pago.getEstado()))
                .observaciones(pago.getObservaciones())
                .referenciaPago(pago.getReferenciaPago())
                .fechaCreacion(pago.getFechaCreacion())
                .fechaExpiracion(pago.getFechaExpiracion())
                .codigoOperacion(pago.getCodigoOperacion())
                // ========== AGREGAR ESTOS CAMPOS NUEVOS ==========
                .urlCaptura(pago.getUrlCaptura())
                .hashImagen(pago.getHashImagen())
                .montoDetectado(pago.getMontoDetectado())
                .fechaDetectada(pago.getFechaDetectada())
                .motivoRechazo(pago.getMotivoRechazo())
                // ========== FIN ==========
                .build();
    }
    

    public ResultadoResponse<GraficoTresData> graficoTresList(Integer mes){
        GraficoTresData data = pagoRepository.graficoTresList(mes);

        if (data != null){
         log.info("Se obtuvo la lista: {}", data);
         return  ResultadoResponse.success("Lista obtenida: ", data);
        }
        log.error("No se obtuvbo la lista {}", (Object) null);
        return  ResultadoResponse.error("Lista obtenida: ",  null);
    }
    
    public byte[] generarReporteIngresos() {
        List<Pago> pagos = pagoRepository.findAll();

        List<ReporteIngresoItem> items = pagos.stream().map(p -> {
            ReporteIngresoItem item = new ReporteIngresoItem();
            item.setIdPago(p.getIdPago());
            item.setReferenciaPago(p.getReferenciaPago());
            item.setTipoPago(obtenerDescripcionTipoPago(p.getTipoPago()));
            item.setMetodoPago(obtenerDescripcionMetodoPago(p.getMetodoPago()));
            item.setMonto(p.getMonto());
            item.setEstado(p.getEstado().name());
            item.setEstadoDescripcion(obtenerDescripcionEstado(p.getEstado()));
            item.setFechaPago(p.getFechaPago());
            item.setFechaCreacion(p.getFechaCreacion());
            item.setCodigoOperacion(p.getCodigoOperacion());
            item.setObservaciones(p.getObservaciones());
            return item;
        }).toList();

        return pdfIngresosService.generarReporteIngresos(items);
    }
}
