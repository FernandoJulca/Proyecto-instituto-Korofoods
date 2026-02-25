package com.koroFoods.reservationService.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.koroFoods.reservationService.dto.CodigoVerificacionResponse;
import com.koroFoods.reservationService.dto.EnviarCodigoRequest;
import com.koroFoods.reservationService.dto.ResultadoResponse;
import com.koroFoods.reservationService.dto.VerificarCodigoRequest;
import com.koroFoods.reservationService.service.VerificacionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/verificacion")
@RequiredArgsConstructor
@Validated
public class VerificacionController {
    
    private final VerificacionService verificacionService;
    
    /**
     * Envía código de verificación por SMS o EMAIL
     */
    @PostMapping("/enviar-codigo")
    public ResultadoResponse<CodigoVerificacionResponse> enviarCodigo(
            @Valid @RequestBody EnviarCodigoRequest request) {
        
        CodigoVerificacionResponse response = verificacionService.enviarCodigoVerificacion(request);
        return ResultadoResponse.success("Código enviado", response);
    }
    
    /**
     * Verifica el código y marca la reserva como ASISTIDA
     */
    @PostMapping("/verificar")
    public ResultadoResponse<String> verificarCodigo(
            @Valid @RequestBody VerificarCodigoRequest request) {
        
        return verificacionService.verificarCodigo(request);
    }
    
    /**
     * Reenvía código de verificación
     */
    @PostMapping("/reenviar-codigo")
    public ResultadoResponse<CodigoVerificacionResponse> reenviarCodigo(
            @Valid @RequestBody EnviarCodigoRequest request) {
        
        CodigoVerificacionResponse response = verificacionService.enviarCodigoVerificacion(request);
        return ResultadoResponse.success("Código reenviado", response);
    }
}
