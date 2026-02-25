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

import com.koroFoods.eventService.dtos.EventTableRequest;
import com.koroFoods.eventService.dtos.EventTableResponse;
import com.koroFoods.eventService.service.EventoMesaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/evento-mesas")
@RequiredArgsConstructor
public class EventoMesaController {

	private final EventoMesaService eventoMesaService;

    @PostMapping
    public ResponseEntity<EventTableResponse> asignarMesa(@Valid @RequestBody EventTableRequest request) {
    	EventTableResponse response = eventoMesaService.asignarMesaAEvento(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EventTableResponse>> listarTodos() {
        List<EventTableResponse> eventoMesas = eventoMesaService.listarTodos();
        return ResponseEntity.ok(eventoMesas);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<EventTableResponse>> listarActivos() {
        List<EventTableResponse> eventoMesas = eventoMesaService.listarActivos();
        return ResponseEntity.ok(eventoMesas);
    }

    @GetMapping("/evento/{idEvento}")
    public ResponseEntity<List<EventTableResponse>> listarPorEvento(@PathVariable Integer idEvento) {
        List<EventTableResponse> eventoMesas = eventoMesaService.listarPorEvento(idEvento);
        return ResponseEntity.ok(eventoMesas);
    }

    @GetMapping("/mesa/{idMesa}")
    public ResponseEntity<List<EventTableResponse>> listarPorMesa(@PathVariable Integer idMesa) {
        List<EventTableResponse> eventoMesas = eventoMesaService.listarPorMesa(idMesa);
        return ResponseEntity.ok(eventoMesas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventTableResponse> buscarPorId(@PathVariable Integer id) {
    	EventTableResponse response = eventoMesaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventTableResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody EventTableRequest request) {
    	EventTableResponse response = eventoMesaService.actualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        eventoMesaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    
}
