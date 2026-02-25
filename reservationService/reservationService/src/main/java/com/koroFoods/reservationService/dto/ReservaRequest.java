package com.koroFoods.reservationService.dto;

import java.time.LocalDateTime;


import lombok.Data;

@Data
public class ReservaRequest {

    private Integer idUsuario;
    private Integer idMesa;
    
   
    private String fechaHora;
    private Integer idEvento; // null si es reserva normal
    private String observaciones;
    
}
