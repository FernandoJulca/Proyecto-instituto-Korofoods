package com.koroFoods.eventService.dtos;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventResquest {

	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
	private String nombre;

	@Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
	private String descripcion;

	private Integer idTematica;

	@NotNull(message = "La fecha de inicio del evento es obligatoria")
	
	private LocalDateTime fechaInicio;
	
	@NotNull(message = "La fecha de fin del evento es obligatoria")
	
	private LocalDateTime fechaFin;

	@NotNull(message = "El costo es obligatorio")
	@DecimalMin(value = "0.01", inclusive = false, message = "El costo debe ser mayor a 0")
	private BigDecimal costo;

	private String imagen;
	
	private String imagenBase64;
}
