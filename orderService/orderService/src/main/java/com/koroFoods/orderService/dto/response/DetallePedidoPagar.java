package com.koroFoods.orderService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoPagar {

    private Integer idCliente;
    private String nombreCliente;
    private Integer totalPlatos;
    private String metodoPago;

    private BigDecimal totalPagar;
}
