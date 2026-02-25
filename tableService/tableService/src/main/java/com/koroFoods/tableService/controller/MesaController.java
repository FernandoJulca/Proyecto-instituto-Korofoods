package com.koroFoods.tableService.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.koroFoods.tableService.dto.MesaRequest;
import com.koroFoods.tableService.dto.MesaResponse;
import com.koroFoods.tableService.service.MesaAdminService;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/mesa")
@RequiredArgsConstructor
public class MesaController {
	

	
	private final MesaAdminService mesaAdminService;

    @PostMapping
    public ResponseEntity<MesaResponse> crear(@Valid @RequestBody MesaRequest request) {
        MesaResponse response = mesaAdminService.crear(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MesaResponse>> listarTodas() {
        List<MesaResponse> mesas = mesaAdminService.listarTodas();
        return ResponseEntity.ok(mesas);
    }

    @GetMapping("/activas")
    public ResponseEntity<List<MesaResponse>> listarActivas() {
        List<MesaResponse> mesas = mesaAdminService.listarActivas();
        return ResponseEntity.ok(mesas);
    }

    @GetMapping("/zona/{zona}")
    public ResponseEntity<List<MesaResponse>> listarPorZona(@PathVariable String zona) {
        List<MesaResponse> mesas = mesaAdminService.listarPorZona(zona);
        return ResponseEntity.ok(mesas);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<MesaResponse>> listarPorEstado(@PathVariable String estado) {
        List<MesaResponse> mesas = mesaAdminService.listarPorEstado(estado);
        return ResponseEntity.ok(mesas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MesaResponse> buscarPorId(@PathVariable Integer id) {
        MesaResponse response = mesaAdminService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MesaResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody MesaRequest request) {
        MesaResponse response = mesaAdminService.actualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/estado/{nuevoEstado}")
    public ResponseEntity<MesaResponse> cambiarEstado(
            @PathVariable Integer id,
            @PathVariable String nuevoEstado) {
        MesaResponse response = mesaAdminService.cambiarEstado(id, nuevoEstado);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        mesaAdminService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
