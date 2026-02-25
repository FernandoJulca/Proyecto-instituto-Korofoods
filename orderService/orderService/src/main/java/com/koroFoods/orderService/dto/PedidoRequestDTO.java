package com.koroFoods.orderService.dto;

import java.util.List;

import lombok.Data;

@Data
public class PedidoRequestDTO {
    private Integer idMesa;
    private Integer idUsuario;
    private Integer idReserva;
    private List<DetallePedidoRequestDTO> detalles;
}

