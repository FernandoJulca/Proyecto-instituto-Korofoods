package com.koroFoods.orderService.dto;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlatosMasVendidosDTO {

	private Integer idPlato;
	private String nombrePlato;
	private String tipoPlato;
	private Long cantidadVendida;
	private BigDecimal totalGenerado;
}
