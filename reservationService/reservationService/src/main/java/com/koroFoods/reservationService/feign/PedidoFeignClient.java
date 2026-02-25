package com.koroFoods.reservationService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.koroFoods.reservationService.dto.ResultadoResponse;

@FeignClient(name = "orderService")
public interface PedidoFeignClient {
    
    @GetMapping("/pedido/feign/reserva/{idReserva}")
    ResultadoResponse<PedidoFeign> getPedidoByIdReserva(@PathVariable("idReserva") Integer idReserva);
}