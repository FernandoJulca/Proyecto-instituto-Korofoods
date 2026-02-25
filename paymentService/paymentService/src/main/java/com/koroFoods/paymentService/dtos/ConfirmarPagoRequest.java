package com.koroFoods.paymentService.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmarPagoRequest {

	@NotNull(message = "El ID del pago es obligatorio")
    private Integer idPago;

    @NotBlank(message = "El código de operación es obligatorio")
    private String codigoOperacion; 

    private String observaciones;
}
