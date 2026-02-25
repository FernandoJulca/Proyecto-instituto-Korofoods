package com.koroFoods.orderService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlatoStockDto {

    private Integer idPlato;
    private String nombre;
    private Integer cantidad;
}
