package com.koroFoods.orderService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GraficoCincoList {
    private Integer idUsuario;
    private Integer completado;
    private String  nombre;
}
