package com.koroFoods.userService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecepcionistaResponse {

    private Integer idUsuario;
    private String nombres;
    private String apePaterno;
    private String imagen;
}
