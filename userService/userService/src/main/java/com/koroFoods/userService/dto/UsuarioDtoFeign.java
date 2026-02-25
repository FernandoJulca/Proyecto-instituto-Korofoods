package com.koroFoods.userService.dto;

import lombok.Data;

@Data
public class UsuarioDtoFeign {
	private Integer idUsuario;
	private String nombres;
	private String apePaterno;
	private String apeMaterno;
	private String correo;
	private String imagen;
	private String telefono;
}
