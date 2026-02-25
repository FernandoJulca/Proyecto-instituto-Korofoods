package com.koroFoods.userService.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GraficoTresDto {
    private Integer pagoYape;
    private Integer pagoTarjeta;
    private Integer pagoEfectivo;
    private Integer pagoPlin;
}
