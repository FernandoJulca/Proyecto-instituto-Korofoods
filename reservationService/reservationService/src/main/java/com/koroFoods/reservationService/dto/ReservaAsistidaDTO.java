package com.koroFoods.reservationService.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReservaAsistidaDTO {

	private Integer idReserva;
	private String nombreCliente;
	private String tipoReserva;
	private LocalDateTime fechaReserva;
	private String observaciones;
	private Integer mesa;
	private String zona;
	private String evento;
	private String tematica;
}
