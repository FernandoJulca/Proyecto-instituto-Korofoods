package com.koroFoods.menuService.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlatoRequest {

	 @NotBlank(message = "El nombre es obligatorio")
	    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
	    private String nombre;

	    @NotNull(message = "El precio es obligatorio")
	    @Positive(message = "El precio debe ser mayor a 0")
	    private BigDecimal precio;

	    @NotNull(message = "El stock es obligatorio")
	    @PositiveOrZero(message = "El stock no puede ser negativo")
	    private Integer stock;

	    @NotNull(message = "El tipo de plato es obligatorio")
	    private String tipoPlato; // "E", "S", "P", "B"

	    private String imagen;
	    
	    private String imagenBase64;
}
