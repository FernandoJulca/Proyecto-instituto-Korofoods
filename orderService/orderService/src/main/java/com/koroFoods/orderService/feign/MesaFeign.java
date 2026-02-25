package com.koroFoods.orderService.feign;

import lombok.Data;

@Data
public class MesaFeign {
	private Integer idMesa;
	private int numeroMesa;
	private int capacidad;
	private String tipo;
	private String estado;
}
