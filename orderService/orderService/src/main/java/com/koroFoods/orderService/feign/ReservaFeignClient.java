package com.koroFoods.orderService.feign;

import com.koroFoods.orderService.dto.ResultadoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "reservationService")
public interface ReservaFeignClient {
    @GetMapping("/reserva/feign/reserva/{idReserva}")
    ResultadoResponse<UsuarioFeign>obtenerUsuarioPorReserva(@PathVariable Integer idReserva);

    @GetMapping("/reserva/feign/list/{idCliente}")
    ResultadoResponse<List<ReservaFeign>> obtenerListaDeReservasPorCliente(@PathVariable Integer idCliente);
}
