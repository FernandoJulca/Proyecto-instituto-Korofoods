package com.koroFoods.reservationService.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.koroFoods.reservationService.config.TwilioConfig;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService implements NotificacionService {
    
    private final TwilioConfig twilioConfig;
    
    @Value("${twilio.country-code:+51}")
    private String countryCode;
    
    @Override
    public void enviarCodigoVerificacion(String numeroTelefono, String codigo, String nombreUsuario) {
        try {

        	String numeroNormalizado = normalizarNumero(numeroTelefono);
            
            String mensaje = String.format(
                "Hola %s, tu código de verificación para tu reserva en KoroFoods es: %s. Válido por 15 minutos.",
                nombreUsuario, codigo
            );
            
            Message message = Message.creator(
                new PhoneNumber(numeroNormalizado),
                new PhoneNumber(twilioConfig.getPhoneNumber()),
                mensaje
            ).create();
            
            log.info("SMS enviado exitosamente a: {}. SID: {}", numeroNormalizado, message.getSid());
            
        } catch (Exception e) {
            log.error("Error al enviar SMS a {}: {}", numeroTelefono, e.getMessage());
            throw new RuntimeException("Error al enviar el SMS de verificación", e);
        }
    }
    
    /**
     * Normaliza el número agregando el prefijo del país si no lo tiene
     */
    private String normalizarNumero(String numero) {
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("Número de teléfono vacío");
        }
        
        // Limpiar espacios y caracteres especiales
        numero = numero.trim().replaceAll("[\\s\\-\\(\\)]", "");
        
        // Si ya tiene el prefijo +, devolverlo tal cual
        if (numero.startsWith("+")) {
            return numero;
        }
        
        // Si empieza con el código sin +, agregarlo
        if (numero.startsWith("51")) {
            return "+" + numero;
        }
        
        // Si es un número local (sin prefijo), agregar código de país
        return countryCode + numero;
    }
}