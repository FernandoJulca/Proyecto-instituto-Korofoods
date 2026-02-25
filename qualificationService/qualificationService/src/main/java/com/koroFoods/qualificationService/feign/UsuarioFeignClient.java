package com.koroFoods.qualificationService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.koroFoods.qualificationService.dto.ResultadoResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "userService")
public interface UsuarioFeignClient {
	@GetMapping("/user/feign/{id}")
    ResultadoResponse<UsuarioFeign> getUsuarioById(@PathVariable Integer id);
	
	@GetMapping("/user/feign/noauth/{id}")
    ResultadoResponse<UsuarioPublicoDTO> getUserByIdNoauth(@PathVariable Integer id);


    //Hacer llamado a la notificacion que solicita el dashboard
    @PostMapping("/dashboard/notification/qualification-actualizado")
    void notificarQualification(@RequestParam Integer mes);
}
