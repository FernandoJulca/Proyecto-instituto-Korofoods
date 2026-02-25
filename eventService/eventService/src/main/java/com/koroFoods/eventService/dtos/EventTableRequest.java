package com.koroFoods.eventService.dtos;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventTableRequest {

	 @NotNull(message = "El ID del evento es obligatorio")
	    private Integer idEvento;

	    @NotNull(message = "El ID de la mesa es obligatorio")
	    private Integer idMesa;

	    @NotNull(message = "La fecha desde es obligatoria")
	    private LocalDateTime fechaDesde;

	    @NotNull(message = "La fecha hasta es obligatoria")
	    private LocalDateTime fechaHasta;
}
