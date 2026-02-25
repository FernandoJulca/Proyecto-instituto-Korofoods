package com.koroFoods.reservationService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.koroFoods.reservationService.dto.ResultadoResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "userService")
public interface UsuarioFeignClient {
	@GetMapping("/user/feign/{id}")
    ResultadoResponse<UsuarioFeign> getUsuarioById(@PathVariable Integer id);

    //Hacer llamado a la notificacion que solicita el dashboard
    @PostMapping("/dashboard/notification/reservation-actualizado")
    void notificarReserva(@RequestParam Integer mes);


    //Hacer llamado a la notificacion que solicita el dashboard
    @PostMapping("/dashboard/notification/reservation-evento")
    void notificarReservaEvento(@RequestParam Integer mes);
}
