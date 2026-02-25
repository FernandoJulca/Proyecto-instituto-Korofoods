package com.koroFoods.userService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerfilUsuarioResponse {

    private Integer idUsuario;
    private String nombres;
    private String apePaterno;
    private String apeMaterno;
    private String correo;
    private String imagen;
    private String direccion;
    private String telefono;
    private String fechaRegistro;
}
