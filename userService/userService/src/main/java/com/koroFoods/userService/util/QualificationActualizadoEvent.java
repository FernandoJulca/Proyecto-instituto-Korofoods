package com.koroFoods.userService.util;

import org.springframework.context.ApplicationEvent;

public class QualificationActualizadoEvent extends ApplicationEvent {

    private final Integer mes;

    public QualificationActualizadoEvent(Object source, Integer mesInyect){
        super(source);
        this.mes = mesInyect;
    }

    public Integer getMes(){
        return mes;
    }
}
