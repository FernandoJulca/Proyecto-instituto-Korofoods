package com.koroFoods.paymentService.dtos;


import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagoResponse {

	private Integer idPago;
	private Integer idReserva;
	private Integer idPedido;
	private Integer idUsuario;
	private String tipoPago;
	private String tipoPagoDescripcion;
	private BigDecimal monto;
	private String metodoPago;
	private String metodoPagoDescripcion;
	private LocalDateTime fechaPago;
	private String estado;
	private String estadoDescripcion;
	private String observaciones;
	private String referenciaPago;
	private LocalDateTime fechaCreacion;
	private LocalDateTime fechaExpiracion;
	private String codigoOperacion;
	private String urlCaptura;
	private String hashImagen;
	private BigDecimal montoDetectado;
	private LocalDateTime fechaDetectada;
	private String motivoRechazo;
}
