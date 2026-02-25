package com.koroFoods.reservationService.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EventoConMesaDto {
    
    // Datos de EventoMesa
    private Integer idEventoMesa;
    
    // Datos de Evento
    private String nombre;
    private String descripcion;
    private String tematica;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String imagen;
    private Boolean activo;
    
    // Datos de Mesa (desde Feign)
    private Integer idMesa;
    private Integer numeroMesa;
    private Integer capacidad;
    private String zona;
}
