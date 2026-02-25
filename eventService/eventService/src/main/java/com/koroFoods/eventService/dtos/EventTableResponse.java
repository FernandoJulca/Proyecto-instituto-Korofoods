package com.koroFoods.eventService.dtos;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventTableResponse {

	private Integer idEventoMesa;
    private EventResponse evento;
    private Integer idMesa;
    private LocalDateTime fechaDesde;
    private LocalDateTime fechaHasta;
    private Boolean activo;
}
