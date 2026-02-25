package com.koroFoods.qualificationService.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPublicoDTO {
    private Integer idUsuario;
    private String nombreCompleto;
    private String imagen;
}