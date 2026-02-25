package com.koroFoods.userService.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GraficoSeisDto {

    private Integer idEntidad;
    private Double promedio;
    private Integer total;
    private String nombre;
}
