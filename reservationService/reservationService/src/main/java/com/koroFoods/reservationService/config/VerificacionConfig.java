package com.koroFoods.reservationService.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "verificacion.codigo")
@Getter
@Setter
public class VerificacionConfig {
    private Integer longitud;           // Lee "verificacion.codigo.longitud"
    private Integer expiracionMinutos;  // Lee "verificacion.codigo.expiracion-minutos"
}
