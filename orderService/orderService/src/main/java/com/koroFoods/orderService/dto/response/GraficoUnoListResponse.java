package com.koroFoods.orderService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GraficoUnoListResponse {

    private Integer idPlato;
    private Integer cantidadPlatos;
    private String nombrePlato;
}
