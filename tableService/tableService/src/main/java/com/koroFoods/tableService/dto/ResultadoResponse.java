package com.koroFoods.tableService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoResponse<T> {
    private boolean valor;
    private String mensaje;
    private T data;

    public ResultadoResponse(boolean valor, String mensaje) {
        this.valor = valor;
        this.mensaje = mensaje;
        this.data = null;
    }

    public static <T> ResultadoResponse<T> success(String mensaje, T data) {
        return new ResultadoResponse<>(true, mensaje, data);
    }

    public static <T> ResultadoResponse<T> success(String mensaje) {
        return new ResultadoResponse<>(true, mensaje, null);
    }

    public static <T> ResultadoResponse<T> error(String mensaje) {
        return new ResultadoResponse<>(false, mensaje, null);
    }

    public static <T> ResultadoResponse<T> error(String mensaje, T data) {
        return new ResultadoResponse<>(false, mensaje, data);
    }
}

