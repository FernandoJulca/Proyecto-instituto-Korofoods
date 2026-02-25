package com.koroFoods.orderService.model;

import com.koroFoods.orderService.enums.EstadoPedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_PEDIDO")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PEDIDO")
    private Integer idPedido;

    @Column(name = "ID_MESA")
    private Integer idMesa;

    @Column(name = "ID_USUARIO")
    private Integer idUsuario;
    
    @Column(name = "ID_RESERVA")
    private Integer idReserva;

    @Column(name = "FECHA_HORA")
    private LocalDateTime fechaHora;

    @Column(name = "SUBTOTAL")
    private BigDecimal subTotal;
    
    @Column(name = "TOTAL")
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO")
    private EstadoPedido estado;

}
