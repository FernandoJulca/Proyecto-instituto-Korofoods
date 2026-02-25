package com.koroFoods.paymentService.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagoAnuladoEvent {

	private Integer idPago;
    private Integer idReserva;
    private Integer idPedido;
    private String motivo;
}
