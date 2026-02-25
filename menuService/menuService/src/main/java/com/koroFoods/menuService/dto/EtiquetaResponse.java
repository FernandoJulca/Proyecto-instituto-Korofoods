package com.koroFoods.menuService.dto;


import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EtiquetaResponse {

	private Integer idEtiqueta;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
}
