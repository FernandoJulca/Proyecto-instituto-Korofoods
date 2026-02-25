package com.koroFoods.eventService.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.koroFoods.eventService.dtos.EventResponse;
import com.koroFoods.eventService.dtos.EventResquest;

import com.koroFoods.eventService.service.EventoService;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {
	private final EventoService eventoService;
	@PostMapping
	public ResponseEntity<EventResponse> crear(@Valid @RequestBody EventResquest request) {
		EventResponse response = eventoService.crear(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<List<EventResponse>> listarTodos() {
		List<EventResponse> eventos = eventoService.listarTodos();
		return ResponseEntity.ok(eventos);
	}

	@GetMapping("/activos")
	public ResponseEntity<List<EventResponse>> listarActivos() {
		List<EventResponse> eventos = eventoService.listarActivos();
		return ResponseEntity.ok(eventos);
	}

	@GetMapping("/futuros")
	public ResponseEntity<List<EventResponse>> listarEventosFuturos() {
		List<EventResponse> eventos = eventoService.listarEventosFuturos();
		return ResponseEntity.ok(eventos);
	}

	@GetMapping("/tematica/{idTematica}")
	public ResponseEntity<List<EventResponse>> listarPorTematica(@PathVariable Integer idTematica) {
		List<EventResponse> eventos = eventoService.listarPorTematica(idTematica);
		return ResponseEntity.ok(eventos);
	}

	@GetMapping("/{id}")
	public ResponseEntity<EventResponse> buscarPorId(@PathVariable Integer id) {
		EventResponse response = eventoService.buscarPorId(id);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<EventResponse> actualizar(@PathVariable Integer id,
			@Valid @RequestBody EventResquest request) {
		EventResponse response = eventoService.actualizar(id, request);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
		eventoService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/reporte/eventos")
	public ResponseEntity<byte[]> reporteEventos() {
	    byte[] pdf = eventoService.generarReporteEventos();
	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-eventos.pdf")
	            .contentType(MediaType.APPLICATION_PDF)
	            .body(pdf);
	}
}
