package com.koroFoods.tableService.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MesaResponse {


	 private Integer idMesa;
	    private Integer numeroMesa;
	    private Integer capacidad;
	    private String zona;
	    private String zonaDescripcion; // "Zona 1" o "Zona 2"
	    private String estado;
	    private String estadoDescripcion; // "Libre", "Asignada", "Ocupada"
	    private Boolean activo;
}
