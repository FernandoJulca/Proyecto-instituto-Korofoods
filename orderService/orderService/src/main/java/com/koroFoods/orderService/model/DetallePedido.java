package com.koroFoods.orderService.model;

import com.koroFoods.orderService.enums.EstadoDetallePedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_DETALLE_PEDIDO")
public class DetallePedido {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DETALLE")
    private Integer idDetalle;

    @Column(name = "ID_PEDIDO")
    private Integer idPedido;

    @Column(name = "ID_PLATO")
    private Integer idPlato;

    @Column(name = "CANTIDAD")
    private Integer cantidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO")
    private EstadoDetallePedido estado;

    @Column(name = "PRECIO_UNITARIO")
    private BigDecimal precioUnitario;

    @Column(name = "SUBTOTAL")
    private BigDecimal subtotal;
}
