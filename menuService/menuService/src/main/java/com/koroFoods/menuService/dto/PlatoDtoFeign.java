package com.koroFoods.menuService.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class PlatoDtoFeign {
    private Integer idPlato;
    private String nombre;
    private String tipoPlato;
    private String imagen;
    private Integer stock;
    private BigDecimal precio;
    private List<EtiquetaDtoFeign> etiquetas;
}
