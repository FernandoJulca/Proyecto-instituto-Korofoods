package com.koroFoods.menuService.controller;

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

import com.koroFoods.menuService.dto.PlatoRequest;
import com.koroFoods.menuService.dto.PlatoResponse;
import com.koroFoods.menuService.service.MenuService;
import com.koroFoods.menuService.service.PlatoAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/platos")
@RequiredArgsConstructor
public class PlatoAdminController {

	private final PlatoAdminService platoAdminService;
	private final MenuService menuService;
	
    @PostMapping
    public ResponseEntity<PlatoResponse> crear(@Valid @RequestBody PlatoRequest request) {
        PlatoResponse response = platoAdminService.crear(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PlatoResponse>> listarTodos() {
        List<PlatoResponse> platos = platoAdminService.listarTodos();
        return ResponseEntity.ok(platos);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<PlatoResponse>> listarActivos() {
        List<PlatoResponse> platos = platoAdminService.listarActivos();
        return ResponseEntity.ok(platos);
    }

    @GetMapping("/ordenados")
    public ResponseEntity<List<PlatoResponse>> listarActivosOrdenados() {
        List<PlatoResponse> platos = platoAdminService.listarActivosOrdenados();
        return ResponseEntity.ok(platos);
    }

    @GetMapping("/tipo/{tipoPlato}")
    public ResponseEntity<List<PlatoResponse>> listarPorTipo(@PathVariable String tipoPlato) {
        List<PlatoResponse> platos = platoAdminService.listarPorTipo(tipoPlato);
        return ResponseEntity.ok(platos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlatoResponse> buscarPorId(@PathVariable Integer id) {
        PlatoResponse response = platoAdminService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlatoResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody PlatoRequest request) {
        PlatoResponse response = platoAdminService.actualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        platoAdminService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/reporte/platos")
	public ResponseEntity<byte[]> reportePlatos() {
	    byte[] pdf = menuService.generarReportePlatos();
	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-platos.pdf")
	            .contentType(MediaType.APPLICATION_PDF)
	            .body(pdf);
	}
}
