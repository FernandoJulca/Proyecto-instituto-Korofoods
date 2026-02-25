package com.koroFoods.reservationService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecepcionistaCountsDTO {

    private long reservasHoy;
    private long reservasAsistidas;
    private long reservasPendientes;   // reservasHoy - reservasAsistidas
    private long reservasTomorrow;
}