package com.koroFoods.menuService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.koroFoods.menuService.dto.PlatoEtiquetaRequest;
import com.koroFoods.menuService.service.PlatoEtiquetaAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/plato-etiquetas")
@RequiredArgsConstructor
public class PlatoEtiquetaAdminController {

	private final PlatoEtiquetaAdminService platoEtiquetaAdminService;

    @PostMapping
    public ResponseEntity<Void> asignarEtiquetas(@Valid @RequestBody PlatoEtiquetaRequest request) {
        platoEtiquetaAdminService.asignarEtiquetas(request);
        return ResponseEntity.ok().build();
    }
}
