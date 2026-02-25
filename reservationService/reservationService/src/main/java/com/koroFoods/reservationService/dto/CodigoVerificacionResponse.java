package com.koroFoods.reservationService.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CodigoVerificacionResponse {
    private String mensaje;
    private LocalDateTime fechaExpiracion;
    private String tipoEnvio;
}