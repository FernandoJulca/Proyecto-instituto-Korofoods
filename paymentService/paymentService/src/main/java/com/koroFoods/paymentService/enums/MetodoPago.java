package com.koroFoods.paymentService.enums;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.*;

public enum MetodoPago {

	YAPE("Yape"),
    PLIN("Plin"),
    EFECTIVO("Efectivo"),
    TARJETA("Tarjeta");

    private final String value;

    MetodoPago(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static MetodoPago from(String value) {
        return Arrays.stream(values())
                .filter(m -> m.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Método de pago inválido: " + value)
                );
    }
}
