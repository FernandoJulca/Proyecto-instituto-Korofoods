package com.koroFoods.qualificationService.feign;

import lombok.Data;

@Data
public class PlatoFeign {
	private Integer idPlato;
	private String nombre;
	private String tipoPlato;
	private String imagen;
}
