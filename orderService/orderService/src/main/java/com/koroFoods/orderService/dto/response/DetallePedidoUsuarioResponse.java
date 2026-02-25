package com.koroFoods.orderService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoUsuarioResponse {
    private String nombres;
    private String apePaterno;
    private String apeMaterno;

    private Long entregados;
    private Long pedidos;
    private Long cancelados;
}
