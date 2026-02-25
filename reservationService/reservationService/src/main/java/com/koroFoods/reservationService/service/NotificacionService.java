package com.koroFoods.reservationService.service;

public interface NotificacionService {
    void enviarCodigoVerificacion(String destinatario, String codigo, String nombreUsuario);
}
