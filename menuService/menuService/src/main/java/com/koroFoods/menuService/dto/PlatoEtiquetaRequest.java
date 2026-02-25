package com.koroFoods.menuService.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlatoEtiquetaRequest {

	@NotNull(message = "El ID del plato es obligatorio")
    private Integer idPlato;

    @NotEmpty(message = "Debe seleccionar al menos una etiqueta")
    private List<Integer> idsEtiquetas;
}
