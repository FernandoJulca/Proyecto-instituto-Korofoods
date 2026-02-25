package com.koroFoods.orderService.dto.response;

import com.koroFoods.orderService.enums.EstadoDetallePedido;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoResponse {

    private Integer idReserva;
    private Integer idDetalle;
    private Integer idPedido;
    private Integer idPlato;
    private String imagen;
    private String nombre;
    private Integer cantidad;

    private String estado;
    private BigDecimal precioUnitario;
    private BigDecimal subTotal;

}
