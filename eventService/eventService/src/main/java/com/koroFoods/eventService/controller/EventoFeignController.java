package com.koroFoods.eventService.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.koroFoods.eventService.dtos.EventoConMesaDto;
import com.koroFoods.eventService.dtos.EventoDtoFeign;
import com.koroFoods.eventService.dtos.EventoFeignReserva;
import com.koroFoods.eventService.dtos.ResultadoResponse;
import com.koroFoods.eventService.service.EventoMesaService;
import com.koroFoods.eventService.service.EventoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/evento/feign")
@RequiredArgsConstructor
public class EventoFeignController {
	private final EventoService eventoService;
	private final EventoMesaService eventoMesaService;

	// Endpoint para el feign de la reseña
	@GetMapping
	public ResponseEntity<ResultadoResponse<List<EventoDtoFeign>>> list() {
		ResultadoResponse<List<EventoDtoFeign>> resultado = eventoService.getAllEvents();

		if (resultado.isValor()) {
			return ResponseEntity.status(HttpStatus.OK).body(resultado);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<ResultadoResponse<EventoDtoFeign>> getEventhById(@PathVariable Integer id) {
		ResultadoResponse<EventoDtoFeign> event = eventoService.getEventById(id);
		return ResponseEntity.ok(event);
	}

	// Detalle del evento
	@GetMapping("/validar/{id}")
	public ResponseEntity<ResultadoResponse<EventoFeignReserva>> obtenerEventoValidado(@PathVariable Integer id) {
		ResultadoResponse<EventoFeignReserva> evento = eventoService.buscarEventoParaReserva(id);
		return ResponseEntity.ok(evento);
	}

	@GetMapping("/ocupaciones")
	public ResultadoResponse<Boolean> validarOcupacion(@RequestParam Integer mesaId, @RequestParam Integer eventoId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {

		boolean asignada = eventoMesaService.mesaAsignadaAlEvento(mesaId, eventoId, desde, hasta);

		return ResultadoResponse.success("Validación realizada", asignada);
	}

	@GetMapping("/mesas/{idEvento}")
	public ResponseEntity<ResultadoResponse<List<EventoConMesaDto>>> listarMesasPorEvento(@PathVariable Integer idEvento,
			@RequestParam(required = false) Integer cantidadPersonas) {

		ResultadoResponse<List<EventoConMesaDto>> resultado = eventoMesaService
				.listarMesasPorEventoParaReserva(idEvento, cantidadPersonas);

		return ResponseEntity.ok(resultado);
	}

	@GetMapping("/dashboard/hoy")
	public ResponseEntity<ResultadoResponse<List<EventoFeignReserva>>> listarEventosDelDia() {
	    return ResponseEntity.ok(eventoService.listarEventosDelDia());
	}
	
}
