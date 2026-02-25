package com.koroFoods.menuService.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ReportePlatoItem {

	private Integer idPlato;
	private String nombre;
	private BigDecimal precio;
	private Integer stock;
	private String tipoPlato;
	private String tipoPlatoDescripcion;
	private Boolean activo;
	private List<String> etiquetas;
}
