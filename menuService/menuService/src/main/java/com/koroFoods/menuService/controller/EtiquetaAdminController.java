package com.koroFoods.menuService.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.koroFoods.menuService.dto.EtiquetaRequest;
import com.koroFoods.menuService.dto.EtiquetaResponse;
import com.koroFoods.menuService.service.EtiquetaAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/etiquetas")
@RequiredArgsConstructor
public class EtiquetaAdminController {

	private final EtiquetaAdminService etiquetaAdminService;

    @PostMapping
    public ResponseEntity<EtiquetaResponse> crear(@Valid @RequestBody EtiquetaRequest request) {
        EtiquetaResponse response = etiquetaAdminService.crear(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EtiquetaResponse>> listarTodas() {
        List<EtiquetaResponse> etiquetas = etiquetaAdminService.listarTodas();
        return ResponseEntity.ok(etiquetas);
    }

    @GetMapping("/activas")
    public ResponseEntity<List<EtiquetaResponse>> listarActivas() {
        List<EtiquetaResponse> etiquetas = etiquetaAdminService.listarActivas();
        return ResponseEntity.ok(etiquetas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EtiquetaResponse> buscarPorId(@PathVariable Integer id) {
        EtiquetaResponse response = etiquetaAdminService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EtiquetaResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody EtiquetaRequest request) {
        EtiquetaResponse response = etiquetaAdminService.actualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        etiquetaAdminService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
