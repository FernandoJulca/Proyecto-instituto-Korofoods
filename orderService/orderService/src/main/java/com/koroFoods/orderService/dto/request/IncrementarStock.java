package com.koroFoods.orderService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IncrementarStock {
    private Integer idPlato;
    private Integer cantidad;
}

