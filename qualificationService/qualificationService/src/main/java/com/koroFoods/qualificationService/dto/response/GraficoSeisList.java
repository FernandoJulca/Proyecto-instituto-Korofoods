package com.koroFoods.qualificationService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GraficoSeisList {
    private Integer idEntidad;
    private Double promedio;
    private Integer total;
    private String nombre;
}
