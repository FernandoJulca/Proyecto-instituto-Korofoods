package com.koroFoods.orderService.feign;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PlatoFeign {
	private Integer idPlato;
	private String nombre;
	private String tipoPlato;
	private String imagen;
    private Integer stock;
    private BigDecimal precio;
}
