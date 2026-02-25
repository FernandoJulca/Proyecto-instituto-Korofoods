package com.koroFoods.reservationService.dto;


import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReporteReservasRequest {

	private LocalDate fechaInicio;
	private LocalDate fechaFin;
	private String estado; // PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA
	private String zona; // Z1, Z2, Z3, Z4
}
