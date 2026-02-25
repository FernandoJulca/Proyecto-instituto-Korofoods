package com.koroFoods.paymentService.enums;

import lombok.Getter;

@Getter
public enum MotivoRechazo {

	 HASH_DUPLICADO("Esta captura ya fue utilizada en otro pago"),
	    CODIGO_DUPLICADO("Este código de operación ya fue registrado"),
	    MONTO_INCORRECTO("El monto detectado no coincide con el esperado"),
	    MONTO_NO_DETECTADO("No se pudo detectar el monto en la captura"),
	    FECHA_INVALIDA("La fecha de la transacción es muy antigua (más de 24 horas)"),
	    FECHA_NO_DETECTADA("No se pudo detectar la fecha en la captura"),
	    CODIGO_NO_DETECTADO("No se pudo detectar el código de operación en la captura"),
	    IMAGEN_NO_LEGIBLE("La imagen no es legible. Intenta con mejor calidad o iluminación"),
	    METODO_PAGO_INCORRECTO("La captura no corresponde al método de pago seleccionado"),
	    ERROR_OCR("Error al procesar la imagen. Intenta nuevamente");

	    private final String mensaje;

	    MotivoRechazo(String mensaje) {
	        this.mensaje = mensaje;
	    }
}
