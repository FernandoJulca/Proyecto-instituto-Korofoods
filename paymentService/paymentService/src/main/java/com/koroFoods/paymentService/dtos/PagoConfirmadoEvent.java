package com.koroFoods.paymentService.dtos;


import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.koroFoods.paymentService.enums.MetodoPago;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagoConfirmadoEvent {

	private Integer idPago;
    private Integer idReserva;
    private Integer idPedido;
    private Integer idUsuario;
    private String tipoPago; // DR o PP
    private BigDecimal monto;
    private MetodoPago metodoPago;
    private LocalDateTime fechaPago;
    private String codigoOperacion;
}
