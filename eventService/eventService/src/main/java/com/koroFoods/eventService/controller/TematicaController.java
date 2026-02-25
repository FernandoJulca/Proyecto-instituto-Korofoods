package com.koroFoods.eventService.controller;

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

import com.koroFoods.eventService.dtos.TematicRequest;
import com.koroFoods.eventService.dtos.TematicResponse;
import com.koroFoods.eventService.service.TematicaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tematicas")
@RequiredArgsConstructor
public class TematicaController {

	private final TematicaService tematicaService;

    @PostMapping
    public ResponseEntity<TematicResponse> crear(@Valid @RequestBody TematicRequest request) {
        TematicResponse response = tematicaService.crear(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TematicResponse>> listarTodas() {
        List<TematicResponse> tematicas = tematicaService.listarTodas();
        return ResponseEntity.ok(tematicas);
    }

    @GetMapping("/activas")
    public ResponseEntity<List<TematicResponse>> listarActivas() {
        List<TematicResponse> tematicas = tematicaService.listarActivas();
        return ResponseEntity.ok(tematicas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TematicResponse> buscarPorId(@PathVariable Integer id) {
    	TematicResponse response = tematicaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TematicResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody TematicRequest request) {
    	TematicResponse response = tematicaService.actualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        tematicaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
