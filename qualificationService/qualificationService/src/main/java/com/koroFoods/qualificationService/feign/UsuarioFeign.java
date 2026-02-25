package com.koroFoods.qualificationService.feign;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioFeign {
    private Integer idUsuario;
    private String nombres;
	private String apePaterno;
    private String apeMaterno;
    private String correo;
    private String imagen;
}
