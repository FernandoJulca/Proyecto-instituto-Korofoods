package com.koroFoods.eventService.dtos;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReporteEventoItem {

	
	private Integer idEvento;
    private String nombre;
    private String descripcion;
    private String tematica;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private BigDecimal costo;
    private Boolean activo;
    private String estado; // "Próximo", "En curso", "Finalizado"
}
