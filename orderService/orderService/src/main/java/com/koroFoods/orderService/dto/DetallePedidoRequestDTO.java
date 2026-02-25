package com.koroFoods.orderService.dto;

import lombok.Data;

@Data
public class DetallePedidoRequestDTO {
    private Integer idPlato;
    private Integer cantidad;
}
