package com.koroFoods.qualificationService.dto;
import lombok.Data;

@Data
public class ResenaListResponse {
    private Integer idResena;
	private Integer idUsuario;
	private String nombreUsuarioCompleto;
	private String imagenUsuario;
	private Integer idEntidad;
	private String nombreEntidad;
	private String imagenEntidad;
	private int calificacion;
	private String comentario;
}
