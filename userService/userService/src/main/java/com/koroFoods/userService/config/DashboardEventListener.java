package com.koroFoods.userService.config;

import com.koroFoods.userService.service.DashboardService;
import com.koroFoods.userService.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DashboardEventListener {

    private final DashboardService dashboardService;


    @EventListener
    public void onOrderActualizado(OrderActualizadoEvent event){
        dashboardService.enviarGraficoUno(event.getMes());
        dashboardService.enviarGraficoCinco(event.getMes());
    }

    @EventListener
    public void onPaymetActualzado(PaymentActualizadoEvent event){
        dashboardService.enviarGraficoTres(event.getMes());
    }

    @EventListener
    public void onQualificationActualizado(QualificationActualizadoEvent event){
        dashboardService.enviarGraficoSeis(event.getMes());
    }

    @EventListener
    public void onReservationActualizado(ReservationActualizadoEvent event){
        dashboardService.enviarGraficoDos(event.getMes());

    }

    @EventListener
    public void onReservaEventoActualizada(ReservationEventoActualizadoEvent event) {
        dashboardService.enviarGraficoCuatro(event.getMes());
    }


}
