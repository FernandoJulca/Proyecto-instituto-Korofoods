package com.koroFoods.qualificationService.handler;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.koroFoods.qualificationService.dto.ResultadoResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResultadoResponse<?>> handleNotFound(NotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResultadoResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(feign.FeignException.class)
    public ResponseEntity<ResultadoResponse<?>> handleFeignError(feign.FeignException ex) {
        String mensaje = ex.contentUTF8();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResultadoResponse.error(mensaje));
    }
}
