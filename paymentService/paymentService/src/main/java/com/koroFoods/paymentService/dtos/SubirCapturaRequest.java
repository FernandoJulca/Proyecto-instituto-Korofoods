package com.koroFoods.paymentService.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class SubirCapturaRequest {

	 @NotNull(message = "El ID del pago es obligatorio")
	    private Integer idPago;

	    @NotBlank(message = "La imagen es obligatoria")
	    private String imagenBase64; // Imagen en Base64 desde el frontend

	    @NotBlank(message = "El método de pago es obligatorio")
	    private String metodoPago; // "YAPE" o "PLIN" (para validar que coincida)
}
