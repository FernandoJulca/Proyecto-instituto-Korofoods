package com.koroFoods.orderService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoMeseroResponse {

    private String nombres;
    private String apePaterno;
    private String apeMaterno;

    private Long pedidosTotales;
    private Long clientesTotales;
    private Long pedidosCompletados;
}
