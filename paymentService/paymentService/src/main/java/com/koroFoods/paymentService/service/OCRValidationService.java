package com.koroFoods.paymentService.service;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.koroFoods.paymentService.enums.MetodoPago;
import com.koroFoods.paymentService.enums.MotivoRechazo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j

public class OCRValidationService {

	@Value("${pago.validacion.margen-monto:0.50}")
    private BigDecimal margenMonto;

    @Value("${pago.validacion.horas-limite:24}")
    private int horasLimite;

    @Data
    public static class ResultadoOCR {
        private boolean valido;
        private MotivoRechazo motivoRechazo;
        private String codigoOperacion;
        private BigDecimal montoDetectado;
        private LocalDateTime fechaDetectada;
    }

    public ResultadoOCR validarTextoOCR(
            String textoExtraido, 
            BigDecimal montoEsperado, 
            MetodoPago metodoPago
    ) {
        ResultadoOCR resultado = new ResultadoOCR();
        resultado.setValido(false);

        log.info("Validando texto OCR para método: {}, monto esperado: {}", metodoPago, montoEsperado);
        log.debug("Texto completo: {}", textoExtraido);

        // 1. Extraer código de operación
        String codigo = extraerCodigoOperacion(textoExtraido, metodoPago);
        if (codigo == null) {
            log.warn("No se detectó código de operación");
            resultado.setMotivoRechazo(MotivoRechazo.CODIGO_NO_DETECTADO);
            return resultado;
        }
        resultado.setCodigoOperacion(codigo);
        log.info("Código operación detectado: {}", codigo);

        // 2. Extraer monto
        BigDecimal monto = extraerMonto(textoExtraido);
        if (monto == null) {
            log.warn("No se detectó monto");
            resultado.setMotivoRechazo(MotivoRechazo.MONTO_NO_DETECTADO);
            return resultado;
        }
        resultado.setMontoDetectado(monto);
        log.info("Monto detectado: {}", monto);

        // 3. Validar monto
        BigDecimal diferencia = montoEsperado.subtract(monto).abs();
        if (diferencia.compareTo(margenMonto) > 0) {
            log.warn("Monto incorrecto. Esperado: {}, Detectado: {}, Diferencia: {}", 
                    montoEsperado, monto, diferencia);
            resultado.setMotivoRechazo(MotivoRechazo.MONTO_INCORRECTO);
            return resultado;
        }

        // 4. Extraer fecha
        LocalDateTime fecha = extraerFecha(textoExtraido);
        if (fecha == null) {
            log.warn("No se detectó fecha");
            resultado.setMotivoRechazo(MotivoRechazo.FECHA_NO_DETECTADA);
            return resultado;
        }
        resultado.setFechaDetectada(fecha);
        log.info("Fecha detectada: {}", fecha);

        // 5. Validar que la fecha sea reciente
        LocalDateTime limiteAntiguo = LocalDateTime.now().minusHours(horasLimite);
        if (fecha.isBefore(limiteAntiguo)) {
            log.warn("Fecha muy antigua. Detectada: {}, Límite: {}", fecha, limiteAntiguo);
            resultado.setMotivoRechazo(MotivoRechazo.FECHA_INVALIDA);
            return resultado;
        }

        // ✅ TODO OK
        resultado.setValido(true);
        log.info("✅ Validación OCR exitosa");
        return resultado;
    }

    private String extraerCodigoOperacion(String texto, MetodoPago metodo) {
        Pattern pattern;
        
        if (metodo == MetodoPago.YAPE) {
            // Buscar: YP-123456789 o YP 123456789
            pattern = Pattern.compile("YP[\\s-]?(\\d{9})", Pattern.CASE_INSENSITIVE);
        } else if (metodo == MetodoPago.PLIN) {
            // Buscar: PL-123456789 o PL 123456789
            pattern = Pattern.compile("PL[\\s-]?(\\d{9})", Pattern.CASE_INSENSITIVE);
        } else {
            return null;
        }

        Matcher matcher = pattern.matcher(texto);
        if (matcher.find()) {
            String codigo = metodo.name() + "-" + matcher.group(1);
            return codigo;
        }

        return null;
    }

    private BigDecimal extraerMonto(String texto) {
        // Buscar patrones: S/ 15.00, S/. 15.00, 15.00, S/15.00
        Pattern pattern = Pattern.compile("S/?\\s*\\.?\\s*(\\d+\\.\\d{2})");
        Matcher matcher = pattern.matcher(texto);

        if (matcher.find()) {
            try {
                return new BigDecimal(matcher.group(1));
            } catch (NumberFormatException e) {
                log.error("Error al parsear monto: {}", matcher.group(1), e);
            }
        }

        return null;
    }

    private LocalDateTime extraerFecha(String texto) {
        // Patrones comunes: 09/02/2026 14:30, 09-02-2026 14:30, 9/2/2026 2:30 PM
        
        // Patrón 1: dd/MM/yyyy HH:mm
        Pattern pattern1 = Pattern.compile("(\\d{1,2})[/-](\\d{1,2})[/-](\\d{4})\\s+(\\d{1,2}):(\\d{2})");
        Matcher matcher1 = pattern1.matcher(texto);
        
        if (matcher1.find()) {
            try {
                int dia = Integer.parseInt(matcher1.group(1));
                int mes = Integer.parseInt(matcher1.group(2));
                int anio = Integer.parseInt(matcher1.group(3));
                int hora = Integer.parseInt(matcher1.group(4));
                int minuto = Integer.parseInt(matcher1.group(5));
                
                return LocalDateTime.of(anio, mes, dia, hora, minuto);
            } catch (Exception e) {
                log.error("Error al parsear fecha", e);
            }
        }

        // Patrón 2: solo fecha dd/MM/yyyy (asumir hora actual)
        Pattern pattern2 = Pattern.compile("(\\d{1,2})[/-](\\d{1,2})[/-](\\d{4})");
        Matcher matcher2 = pattern2.matcher(texto);
        
        if (matcher2.find()) {
            try {
                int dia = Integer.parseInt(matcher2.group(1));
                int mes = Integer.parseInt(matcher2.group(2));
                int anio = Integer.parseInt(matcher2.group(3));
                
                return LocalDateTime.of(anio, mes, dia, 12, 0); // Asumir mediodía
            } catch (Exception e) {
                log.error("Error al parsear fecha simple", e);
            }
        }

        return null;
    }
}
