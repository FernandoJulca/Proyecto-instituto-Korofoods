package com.koroFoods.orderService.feign;

import com.koroFoods.orderService.dto.request.IncrementarStock;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.koroFoods.orderService.dto.ResultadoResponse;


@FeignClient(name = "menuService")
public interface PlatoFeignClient {
	@GetMapping("/menu/feign/{id}")
    ResultadoResponse<PlatoFeign> getDishById(@PathVariable Integer id);
	
	@PutMapping("/menu/feign/substract-stock/{idPlato}/{cantidadVendida}")
    ResultadoResponse<PlatoFeign> substractStockOrder(
			@PathVariable Integer idPlato, @PathVariable Integer cantidadVendida);

    @PutMapping("/menu/feign/newStock")
    ResultadoResponse<PlatoFeign>incrementarStock(
            @RequestBody IncrementarStock request
            );
}
