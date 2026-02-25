package com.koroFoods.eventService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_TEMATICA")
public class Tematica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TEMATICA")
    private Integer idTematica;

    @Column(name = "NOMBRE")
    private String nombre;
    
    @Column(name="ACTIVO")
    private Boolean activo;
}
