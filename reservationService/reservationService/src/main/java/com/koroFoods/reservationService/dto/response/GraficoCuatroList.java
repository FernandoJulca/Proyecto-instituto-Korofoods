package com.koroFoods.reservationService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GraficoCuatroList {
    private Integer idEvento;
    private Integer cantidad;
    private String nombre;
}
