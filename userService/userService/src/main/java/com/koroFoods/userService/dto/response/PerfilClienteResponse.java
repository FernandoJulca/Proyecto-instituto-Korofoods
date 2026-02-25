package com.koroFoods.userService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerfilClienteResponse {

    private Integer idUsuario;
    private String nombres;
    private String apellidos;
    private String correo;
    private String nroDoc;
    private String imagen;
    private String direccion;
    private String telefono;

    //Falta agregar mas datos para un perfil mas detallado :)
}
