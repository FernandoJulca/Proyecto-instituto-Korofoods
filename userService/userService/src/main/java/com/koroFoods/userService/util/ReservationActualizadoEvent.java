package com.koroFoods.userService.util;

import org.springframework.context.ApplicationEvent;

public class ReservationActualizadoEvent extends ApplicationEvent {

    private final Integer mes;

    public ReservationActualizadoEvent(Object source, Integer mesInyect){
        super(source);
        this.mes = mesInyect;
    }

    public Integer getMes(){
        return mes;
    }
}
