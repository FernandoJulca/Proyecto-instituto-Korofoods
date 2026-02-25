package com.koroFoods.paymentService.dtos;


import lombok.*;
import java.math.BigDecimal;

import com.koroFoods.paymentService.enums.MetodoPago;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QRDataResponse {

	private Integer idPago;
	private String referenciaPago; // Para identificar el pago
	private BigDecimal monto;
	private MetodoPago metodoPago; // YAPE o PLIN
	private String numeroDestino; // Número de teléfono/cuenta para Yape/Plin
	private String nombreDestino; // Nombre del negocio
	private String concepto; // Mensaje que aparecerá en Yape/Plin
	private String qrData; // Datos para generar QR en frontend
	private String fechaExpiracion;
}
