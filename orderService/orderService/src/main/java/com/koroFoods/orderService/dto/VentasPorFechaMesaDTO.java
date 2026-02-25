package com.koroFoods.orderService.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class VentasPorFechaMesaDTO {

	private String fecha; // "2025-02-21"
	private Integer idMesa;
	private Integer numeroMesa;
	private String zona;
	private Long totalPedidos;
	private BigDecimal totalVentas;
}
