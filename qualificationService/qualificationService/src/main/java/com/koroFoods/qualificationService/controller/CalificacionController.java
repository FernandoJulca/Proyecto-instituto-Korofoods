package com.koroFoods.qualificationService.controller;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.koroFoods.qualificationService.dto.ResenaListResponse;
import com.koroFoods.qualificationService.dto.ResenaRequest;
import com.koroFoods.qualificationService.dto.ResultadoResponse;
import com.koroFoods.qualificationService.model.Calificacion;
import com.koroFoods.qualificationService.service.CalificacionService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/calificacion")
public class CalificacionController {
	private final CalificacionService resenaService;
	
	@PostMapping
	public ResponseEntity<ResultadoResponse<Calificacion>> crear(@RequestBody ResenaRequest request) {
	    ResultadoResponse<Calificacion> resultado = resenaService.crearResena(request);
	    
	    if (resultado.isValor()) {
	        return ResponseEntity.status(HttpStatus.CREATED).body(resultado); 
	    } else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
	    }
	}
	
	@GetMapping
	public ResponseEntity<ResultadoResponse<List<ResenaListResponse>>> list(){
		ResultadoResponse<List<ResenaListResponse>> resultado = resenaService.listarResenas();
		
		if(resultado.isValor()) {
			return ResponseEntity.status(HttpStatus.OK).body(resultado);
		}else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
	    }
	}
	
	@GetMapping("/usuario/{id}")
	public ResponseEntity<ResultadoResponse<List<ResenaListResponse>>> resenasPorUsuario(@PathVariable Integer id){
		ResultadoResponse<List<ResenaListResponse>> resultado = resenaService.obtenerResenasPorUsuario(id);
		
		if(resultado.isValor()) {
			return ResponseEntity.status(HttpStatus.OK).body(resultado);
		}else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
	    }
	}
}
