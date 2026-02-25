package com.koroFoods.qualificationService.dto;

import com.koroFoods.qualificationService.enums.TipoEntidad;

import lombok.Data;

@Data
public class ResenaRequest {
    private Integer idUsuario;
    private TipoEntidad tipoEntidad; 
    private Integer idEntidad;
    private int calificacion;
    private String comentario;
}

