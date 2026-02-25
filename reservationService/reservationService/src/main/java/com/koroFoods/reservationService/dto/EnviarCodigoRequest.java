package com.koroFoods.reservationService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnviarCodigoRequest {
    
    @NotNull(message = "El ID de reserva es obligatorio")
    private Integer reservaId;
    
    @NotBlank(message = "El tipo de envío es obligatorio")
    @Pattern(regexp = "SMS|EMAIL", message = "Tipo de envío debe ser SMS o EMAIL")
    private String tipoEnvio; // SMS o EMAIL
}