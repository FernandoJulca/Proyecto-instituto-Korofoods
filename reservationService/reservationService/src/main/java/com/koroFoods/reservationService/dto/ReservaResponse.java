package com.koroFoods.reservationService.dto;

import java.time.LocalDateTime;

import com.koroFoods.reservationService.enums.EstadoReserva;
import com.koroFoods.reservationService.enums.TipoReserva;
import com.koroFoods.reservationService.enums.Zona;

import lombok.Data;

@Data
public class ReservaResponse {

	private Integer idReserva;
	
	//Cliente
	private String nombreCli;
	private String apellidoPa;
	private String apellidoMa;
	
	//Mesa
	private int numMesa;
    private int capacidad;
    private String zona;

    //Reserva
    private TipoReserva tipoReserva;
    private LocalDateTime fechaHora;
    private EstadoReserva estado; //PENDIENTE, PAGADA, ASISTIDA, CANCELADA, VENCIDA
    private String observaciones; // metodo pago y cuanta spersonas son d la reserva

    //Si hay evento
    private Integer idEvento;
    private String nombreEvento;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
  
}
