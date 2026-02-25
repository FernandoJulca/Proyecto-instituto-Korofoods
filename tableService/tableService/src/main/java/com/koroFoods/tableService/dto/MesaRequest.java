package com.koroFoods.tableService.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MesaRequest {

	 @NotNull(message = "El número de mesa es obligatorio")
	    @Positive(message = "El número de mesa debe ser mayor a 0")
	    private Integer numeroMesa;

	    @NotNull(message = "La capacidad es obligatoria")
	    @Min(value = 1, message = "La capacidad mínima es 1 persona")
	    @Max(value = 20, message = "La capacidad máxima es 20 personas")
	    private Integer capacidad;

	    @NotNull(message = "La zona es obligatoria")
	    private String zona; // "Z1" o "Z2"

	    @NotNull(message = "El estado es obligatorio")
	    private String estado; // "LIBRE", "ASIGNADA", "OCUPADA"
}
