package com.koroFoods.paymentService.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ReporteIngresoItem {

	private Integer idPago;
	private String referenciaPago;
	private String tipoPago; // "Depósito Reserva" o "Pago Pedido"
	private String metodoPago; // "Yape", "Plin", etc.
	private BigDecimal monto;
	private String estado;
	private String estadoDescripcion;
	private LocalDateTime fechaPago;
	private LocalDateTime fechaCreacion;
	private String codigoOperacion;
	private String observaciones;
}
