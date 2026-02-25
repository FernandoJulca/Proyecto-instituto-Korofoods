package com.koroFoods.reservationService.model;

import com.koroFoods.reservationService.enums.EstadoReserva;
import com.koroFoods.reservationService.enums.TipoReserva;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_RESERVA")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_RESERVA")
    private Integer idReserva;

    @Column(name = "ID_USUARIO")
    private Integer idUsuario;

    @Column(name = "ID_MESA")
    private Integer idMesa;
    
    @Column(name = "ID_EVENTO")
    private Integer idEvento;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_RESERVA")
    private TipoReserva tipoReserva;
    
    @Column(name = "FECHA_RESERVA")
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO")
    private EstadoReserva estado;

    @Column(name = "FECHA_REGISTRO")
    private LocalDateTime fechaRegistro;
    
    @Column(name = "OBSERVACIONES")
    private String observaciones;
    
    @Column(name = "CODIGO_VERIFICACION")
    private String codigoVerificacion;
    
    @Column(name = "FECHA_EXPIRACION_CODIGO")
    private LocalDateTime fechaExpCod;
    
    @Column(name = "VERIFICADO")
    private Boolean verificado;
}
