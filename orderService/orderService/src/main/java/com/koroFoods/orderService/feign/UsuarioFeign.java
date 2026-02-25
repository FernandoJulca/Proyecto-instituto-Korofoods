package com.koroFoods.orderService.feign;

import lombok.Data;

@Data
public class UsuarioFeign {
    private Integer idUsuario;
    private String nombres;
	private String apePaterno;
    private String apeMaterno;
    private String correo;
    private String imagen;
}
