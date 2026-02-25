package com.koroFoods.menuService.controller;

import java.time.LocalDate;
import java.util.List;

import com.koroFoods.menuService.dto.request.IncrementarStock;
import com.koroFoods.menuService.dto.request.PlatoStockDto;
import com.koroFoods.menuService.model.Plato;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.koroFoods.menuService.dto.PlatoDtoFeign;
import com.koroFoods.menuService.dto.ResultadoResponse;
import com.koroFoods.menuService.service.MenuService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/menu/feign")
@RequiredArgsConstructor
public class MenuFeignController {
	private final MenuService menuService;

	// Endpoint para restar el stock de los pedidos consumidos
	@PutMapping("/substract-stock/{idPlato}/{cantidadVendida}")
    public ResponseEntity<ResultadoResponse<PlatoDtoFeign>> substractStockOrder(
            @PathVariable Integer idPlato,
            @PathVariable Integer cantidadVendida) {

        ResultadoResponse<PlatoDtoFeign> response =
                menuService.substractStockOrder(idPlato, cantidadVendida);

        if (!response.isValor()) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }
	
	// Endpoint para el feign de la reseña
	@GetMapping
	public ResponseEntity<ResultadoResponse<List<PlatoDtoFeign>>> list(){
		ResultadoResponse<List<PlatoDtoFeign>> resultado = menuService.getAllDish();
		
		if(resultado.isValor()) {
			return ResponseEntity.status(HttpStatus.OK).body(resultado);
		}else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
	    }
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ResultadoResponse<PlatoDtoFeign>> getDishById(@PathVariable Integer id) {
		ResultadoResponse<PlatoDtoFeign> dish = menuService.getDishById(id);
		return ResponseEntity.ok(dish);
	}

    @PutMapping("/newStock")
    public ResponseEntity<ResultadoResponse<PlatoStockDto>> incrementarStock(@RequestBody IncrementarStock request){
        ResultadoResponse<PlatoStockDto> resultado = menuService.aumentarStock(request);

        if(resultado.isValor()) {
            return ResponseEntity.status(HttpStatus.OK).body(resultado);
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
        }
    }
}
