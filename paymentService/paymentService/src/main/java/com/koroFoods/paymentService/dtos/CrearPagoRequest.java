package com.koroFoods.paymentService.dtos;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

import com.koroFoods.paymentService.enums.MetodoPago;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CrearPagoRequest {

	private Integer idReserva;    // Puede ser null si es pedido
    private Integer idPedido;     // Puede ser null si es reserva

    @NotNull(message = "El ID de usuario es obligatorio")
    private Integer idUsuario;

    @NotNull(message = "El tipo de pago es obligatorio")
    private String tipoPago; // "DR" o "PP"

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPago metodoPago; // "YAPE", "PLIN", "EFECTIVO", "TARJETA"

    private String observaciones;
}
