package com.koroFoods.orderService.dto;

import java.math.BigDecimal;

public interface PlatosMasVendidosProjection {

	Integer getIdPlato();
    Long getCantidadVendida();
    BigDecimal getTotalGenerado();
}
