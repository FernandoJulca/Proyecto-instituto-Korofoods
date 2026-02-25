package com.koroFoods.userService.util;

import org.springframework.context.ApplicationEvent;

public class PaymentActualizadoEvent extends ApplicationEvent {

    private final Integer mes;

    public PaymentActualizadoEvent(Object source, Integer mesInyect){
        super(source);
        this.mes = mesInyect;
    }

    public Integer getMes(){
        return mes;
    }
}
