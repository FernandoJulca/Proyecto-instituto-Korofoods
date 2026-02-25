package com.koroFoods.userService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialUsuarioResponse {

    private String chatId;
    private Integer receptorId;
    private String nombre;
    private String apePaterno;
    private String imagen;
    private String ultimoMensaje;
    private LocalDateTime fechaUltimoMensaje;
}

