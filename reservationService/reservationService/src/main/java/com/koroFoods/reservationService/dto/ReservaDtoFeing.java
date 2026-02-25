package com.koroFoods.reservationService.dto;

import lombok.Data;

@Data
public class ReservaDtoFeing {
    private Integer idReserva;
    private Integer idUsuario;
    private String nombreCompletoUsuario;
    private Integer mesa;
    private String estado;
}
