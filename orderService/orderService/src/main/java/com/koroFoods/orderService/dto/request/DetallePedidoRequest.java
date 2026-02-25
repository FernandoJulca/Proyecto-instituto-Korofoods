package com.koroFoods.orderService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoRequest {

    private Integer idPedido;
    private Integer idPlato;
    private Integer cantidad;
}
