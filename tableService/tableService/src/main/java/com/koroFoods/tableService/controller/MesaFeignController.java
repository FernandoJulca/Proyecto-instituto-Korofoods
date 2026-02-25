package com.koroFoods.tableService.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.koroFoods.tableService.dto.MesaDtoFeign;
import com.koroFoods.tableService.dto.ResultadoResponse;
import com.koroFoods.tableService.enums.Zona;
import com.koroFoods.tableService.service.MesaService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/mesa/feign")
@RequiredArgsConstructor
public class MesaFeignController {

	private final MesaService mesaService;

	@GetMapping("/{id}")
	public ResponseEntity<ResultadoResponse<MesaDtoFeign>> getTablehById(@PathVariable Integer id) {
		ResultadoResponse<MesaDtoFeign> table = mesaService.getTableById(id);
		return ResponseEntity.ok(table);
	}

	//Para listar mesas reserva simple
	@GetMapping("/zona/{z}")
	public ResponseEntity<ResultadoResponse<List<MesaDtoFeign>>> obtenerMesasPorZona(@PathVariable Zona z,
			@RequestParam(required = false) Integer cantidadPersonas) {

		ResultadoResponse<List<MesaDtoFeign>> mesasFiltradas = mesaService.obtenerMesasPorZona(z, cantidadPersonas);

		return ResponseEntity.ok(mesasFiltradas);
	}

	@PostMapping("/por-ids")
	public ResponseEntity<ResultadoResponse<List<MesaDtoFeign>>> obtenerMesasPorIds(@RequestBody List<Integer> ids) {
		ResultadoResponse<List<MesaDtoFeign>> mesas = mesaService.obtenerMesasPorIds(ids);
		return ResponseEntity.ok(mesas);
	}
	
	@PutMapping("/estado-ocupada/{id}")
	public ResponseEntity<ResultadoResponse<MesaDtoFeign>> cambiarEstadoOcupada(@PathVariable Integer id) {
		ResultadoResponse<MesaDtoFeign> m = mesaService.cambiarEstadoOcupada(id);
		return ResponseEntity.ok(m);
	}

}
