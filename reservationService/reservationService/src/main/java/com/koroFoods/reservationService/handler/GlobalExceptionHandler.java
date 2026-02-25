package com.koroFoods.reservationService.handler;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.koroFoods.reservationService.dto.ResultadoResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private final ObjectMapper objectMapper = new ObjectMapper();
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResultadoResponse<?>> handleNotFound(NotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResultadoResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(feign.FeignException.class)
    public ResponseEntity<ResultadoResponse<?>> handleFeignError(feign.FeignException ex) {
        String mensaje = "Error al comunicarse con el servicio";
        
        try {
            String errorBody = ex.contentUTF8();
            JsonNode errorJson = objectMapper.readTree(errorBody);
            
            if (errorJson.has("mensaje")) {
                mensaje = errorJson.get("mensaje").asText();
            }
        } catch (Exception e) {
            mensaje = ex.getMessage();
        }
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResultadoResponse.error(mensaje));
    }
}
