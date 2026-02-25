package com.koroFoods.eventService.dtos;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EventResponse {

	private Integer idEvento;
    private String nombre;
    private String descripcion;
    private TematicResponse tematica;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private BigDecimal costo;
    private String imagen;
    private Boolean activo;
}
