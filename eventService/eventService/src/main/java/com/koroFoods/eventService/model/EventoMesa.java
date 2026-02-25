package com.koroFoods.eventService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_EVENTO_MESA")
public class EventoMesa {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EVENTO_MESA")
    private Integer idEventoMesa;

    @ManyToOne
    @JoinColumn(name = "ID_EVENTO", nullable = false)
    private Evento evento;

    @Column(name = "ID_MESA")
    private Integer idMesa;

    @Column(name = "FECHA_DESDE")
    private LocalDateTime fechaDesde;

    @Column(name = "FECHA_HASTA")
    private LocalDateTime fechaHasta;
    
    @Column(name="ACTIVO")
    private Boolean activo;
}
