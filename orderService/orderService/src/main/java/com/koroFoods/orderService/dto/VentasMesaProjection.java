package com.koroFoods.orderService.dto;

import java.math.BigDecimal;

public interface VentasMesaProjection {

	String getFecha();
    Integer getIdMesa();
    Long getTotalPedidos();
    BigDecimal getTotalVentas();
}
