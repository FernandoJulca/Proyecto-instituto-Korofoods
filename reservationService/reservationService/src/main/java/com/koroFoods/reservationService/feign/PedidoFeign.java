package com.koroFoods.reservationService.feign;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import lombok.Data;

@Data
public class PedidoFeign {
	private Integer idPedido;
    private Integer idMesa;
    private String fechaHora;
    private String estado;
    private BigDecimal total;
}
