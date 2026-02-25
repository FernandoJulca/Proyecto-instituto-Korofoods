package com.koroFoods.eventService.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EventoFeignReserva {

    private Integer idEvento;
    private String nombre;
    private String descripcion;
    private String tematica;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer aforo;
    private String imagen;
}
