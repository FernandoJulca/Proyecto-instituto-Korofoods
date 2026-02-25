package com.koroFoods.paymentService.model;

import com.koroFoods.paymentService.enums.EstadoPago;

import com.koroFoods.paymentService.enums.MetodoPago;
import com.koroFoods.paymentService.enums.TipoPago;

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
@Table(name = "TB_PAGO")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PAGO")
    private Integer idPago;

    @Column(name = "ID_USUARIO", nullable = false)
    private Integer idUsuario;

    @Column(name = "ID_RESERVA")
    private Integer idReserva;

    @Column(name = "ID_PEDIDO")
    private Integer idPedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_PAGO", nullable = false)
    private TipoPago tipoPago;

    @Column(name = "MONTO", nullable = false)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "METODO_PAGO", nullable = false)
    private MetodoPago metodoPago;

    @Column(name = "FECHA_PAGO")
    private LocalDateTime fechaPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false)
    private EstadoPago estado;

    @Column(name = "OBSERVACIONES", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "CODIGO_OPERACION", unique = true)
    private String codigoOperacion;

    @Column(name = "REFERENCIA_PAGO", unique = true, nullable = false)
    private String referenciaPago;

    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHA_EXPIRACION")
    private LocalDateTime fechaExpiracion;

    @Column(name = "URL_CAPTURA", length = 500)
    private String urlCaptura;

    @Column(name = "HASH_IMAGEN", length = 64, unique = true)
    private String hashImagen;

    @Column(name = "TEXTO_EXTRAIDO", columnDefinition = "TEXT")
    private String textoExtraido;

    @Column(name = "MONTO_DETECTADO")
    private BigDecimal montoDetectado;

    @Column(name = "FECHA_DETECTADA")
    private LocalDateTime fechaDetectada;

    @Column(name = "MOTIVO_RECHAZO", columnDefinition = "TEXT")
    private String motivoRechazo;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoPago.PEN;
        this.fechaExpiracion = LocalDateTime.now().plusMinutes(30);
    }
}