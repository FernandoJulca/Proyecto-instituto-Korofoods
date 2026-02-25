package com.koroFoods.userService.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GraficoUnoDto {

    private Integer idPlato;
    private Integer cantidadPlatos;
    private String nombrePlato;
}
