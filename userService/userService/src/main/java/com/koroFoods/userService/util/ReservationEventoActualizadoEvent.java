package com.koroFoods.userService.util;

import org.springframework.context.ApplicationEvent;

public class ReservationEventoActualizadoEvent extends ApplicationEvent {
    private final Integer mes;

    public ReservationEventoActualizadoEvent(Object source, Integer mesInyect){
        super(source);
        this.mes = mesInyect;
    }

    public Integer getMes(){
        return mes;
    }
}
