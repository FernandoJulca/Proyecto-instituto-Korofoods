package com.koroFoods.tableService.dto;
import lombok.Data;

@Data
public class MesaDtoFeign {
    private Integer idMesa;
    private int numeroMesa;
    private int capacidad;
    private String tipo; // zona
    private String estado;
}
