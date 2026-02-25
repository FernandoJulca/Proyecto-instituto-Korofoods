package com.koroFoods.userService.controller;

import com.koroFoods.userService.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SocketDashboardController {


    private final DashboardService dashboardService;

    //Al momento que el admin entre precarga los datos iniciales
    //para luego el sistema solo va mandando con los publishers
    //llegando aca para luego mandarlo a angular y actualizar los
    //graficos en tiempo real

    @MessageMapping("/dashboard/init")
    public void inicializarDashboard(@Payload Integer mes){
        System.out.println(">>> Dashboard init recibido, mes: " + mes);
        dashboardService.ActualizarGraficosCompletos(mes);
    }
}
