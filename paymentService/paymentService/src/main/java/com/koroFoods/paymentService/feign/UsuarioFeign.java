package com.koroFoods.paymentService.feign;

import lombok.Data;

@Data
public class UsuarioFeign {
    private Integer idUsuario;
    private String nombres;
	private String apePaterno;
    private String apeMaterno;
    private String correo;
    private String imagen;
    private String telefono;
    
    public String getNombreCompleto() {
        return String.format("%s %s %s", 
            nombres != null ? nombres : "",
            apePaterno != null ? apePaterno : "",
            apeMaterno != null ? apeMaterno : ""
        ).trim();
    }
}
