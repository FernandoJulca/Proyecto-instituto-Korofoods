package com.koroFoods.qualificationService.feign;

import lombok.Data;

@Data
public class EventoFeign {
	 private Integer idEvento;
	    private String nombre;
	    private String descripcion;
	    private String imagen;
}
