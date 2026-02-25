package com.koroFoods.tableService.model;

import com.koroFoods.tableService.enums.EstadoMesa;
import com.koroFoods.tableService.enums.Zona;

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
@Table(name = "TB_MESA")
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MESA")
    private Integer idMesa;

    @Column(name = "NUMERO_MESA")
    private Integer numeroMesa;

    @Column(name = "CAPACIDAD")
    private Integer capacidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "ZONA")
    private Zona zona;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO")
    private EstadoMesa estado;
    
    @Column(name = "ACTIVO")
    private Boolean activo;
}
