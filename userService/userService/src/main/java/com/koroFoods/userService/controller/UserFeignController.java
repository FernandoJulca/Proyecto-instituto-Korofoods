package com.koroFoods.userService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.koroFoods.userService.dto.ResultadoResponse;
import com.koroFoods.userService.dto.UsuarioDtoFeign;
import com.koroFoods.userService.dto.UsuarioPublicoDTO;
import com.koroFoods.userService.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user/feign")
@RequiredArgsConstructor
public class UserFeignController {
	private final UsuarioService usuarioService;

	@GetMapping("/{id}")
	public ResponseEntity<ResultadoResponse<UsuarioDtoFeign>> getUserById(@PathVariable Integer id) {
		ResultadoResponse<UsuarioDtoFeign> user = usuarioService.getUsuarioByIdFeign(id);
		return ResponseEntity.ok(user);
	}
	
	// Implementado para el listado de resenias 
	@GetMapping("/noauth/{id}")
	public ResponseEntity<ResultadoResponse<UsuarioPublicoDTO>> getUserByIdNoauth(@PathVariable Integer id) {
		ResultadoResponse<UsuarioPublicoDTO> user = usuarioService.getUsuarioByIdPublic(id);
		return ResponseEntity.ok(user);
	}
}
