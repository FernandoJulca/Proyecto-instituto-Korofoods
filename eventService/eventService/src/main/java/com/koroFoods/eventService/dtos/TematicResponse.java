package com.koroFoods.eventService.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TematicResponse {

	private Integer idTematica;
    private String nombre;
    private Boolean activo;
}
