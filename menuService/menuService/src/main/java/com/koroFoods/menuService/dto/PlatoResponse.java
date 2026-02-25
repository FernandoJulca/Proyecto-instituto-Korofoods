package com.koroFoods.menuService.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlatoResponse {

	 private Integer idPlato;
	    private String nombre;
	    private BigDecimal precio;
	    private Integer stock;
	    private String tipoPlato;
	    private String tipoPlayoDescripcion; // "Entrada", "Segundo", etc.
	    private String imagen;
	    private Boolean activo;
	    private List<EtiquetaResponse> etiquetas;
}
