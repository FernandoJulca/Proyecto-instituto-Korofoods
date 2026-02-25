package com.koroFoods.reservationService.dto;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomFeignErrorDecoder implements ErrorDecoder {
    
    private final ErrorDecoder defaultErrorDecoder = new Default();
    
    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Error en llamada Feign: {} - Status: {}", methodKey, response.status());
        
        switch (response.status()) {
            case 404:
                return new RuntimeException("Usuario no encontrado en el microservicio");
            case 500:
                return new RuntimeException("Error interno en el microservicio de usuarios");
            default:
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}