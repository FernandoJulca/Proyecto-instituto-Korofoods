package com.koroFoods.userService.dto;

import lombok.Data;

@Data
public class UsuarioPerfilDTO {
	private Integer idUsuario;
	private String nombres;
	private String apePaterno;
	private String apeMaterno;
	private String correo;
	private String imagen;
	private String telefono;
	private String direccion;
	private String tipoDoc;
	private String nroDoc;
	private String fechaRegistro;
	private String distrito;
}
