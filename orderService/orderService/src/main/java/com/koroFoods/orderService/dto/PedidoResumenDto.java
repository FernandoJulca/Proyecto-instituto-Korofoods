package com.koroFoods.orderService.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.koroFoods.orderService.enums.EstadoPedido;

import lombok.Data;

@Data
public class PedidoResumenDto {
	private Integer idPedido;
    private Integer idMesa;
    private LocalDateTime fechaHora;
    private EstadoPedido estado;
    private BigDecimal total;
}
