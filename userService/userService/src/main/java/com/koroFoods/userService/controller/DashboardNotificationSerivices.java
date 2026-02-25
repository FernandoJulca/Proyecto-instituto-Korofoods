package com.koroFoods.userService.controller;

import com.koroFoods.userService.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard/notification")
public class DashboardNotificationSerivices {

    private final ApplicationEventPublisher eventPublisher;


    @PostMapping("/order-actualizado")
    public void notificarOrder(@RequestParam Integer mes){
        eventPublisher.publishEvent(new OrderActualizadoEvent(this, mes));
    }

    @PostMapping("/payment-actualizado")
    public void notificarPayment(@RequestParam Integer mes){
        eventPublisher.publishEvent(new PaymentActualizadoEvent(this, mes));
    }

    @PostMapping("/qualification-actualizado")
    public void notificarQualification(@RequestParam Integer mes){
        eventPublisher.publishEvent(new QualificationActualizadoEvent(this, mes));
    }

    @PostMapping("/reservation-actualizado")
    public void notificarReservation(@RequestParam Integer mes){
        eventPublisher.publishEvent(new ReservationActualizadoEvent(this, mes));
    }
    @PostMapping("/reservation-evento")
    public void notificarReservationEvento(@RequestParam Integer mes){
        eventPublisher.publishEvent(new ReservationEventoActualizadoEvent(this, mes));
    }

}
