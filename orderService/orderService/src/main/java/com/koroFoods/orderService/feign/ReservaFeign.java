package com.koroFoods.orderService.feign;

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
public class ReservaFeign {

    private Integer idReserva;
    private Integer idUsuario;
    private Integer idMesa;
    private Integer idEvento;
    private String tipoReserva;
    private LocalDateTime fechaHora;
    private String estado;
    private LocalDateTime fechaRegistro;
    private String observaciones;
    private String codigoVerificacion;
    private LocalDateTime fechaExpCod;
    private Boolean verificado;
}
