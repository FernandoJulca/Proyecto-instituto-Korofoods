package com.koroFoods.qualificationService.model;

import java.time.LocalDateTime;

import com.koroFoods.qualificationService.enums.EstadoResena;
import com.koroFoods.qualificationService.enums.TipoEntidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_CALIFICACION")
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CALIFICACION")
    private Integer idCalificacion;

    @Column(name = "ID_USUARIO")
    private Integer idUsuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_ENTIDAD")
    private TipoEntidad tipoEntidad;

    @Column(name = "ID_ENTIDAD")
    private Integer idEntidad;

    @Column(name = "PUNTUACION")
    private int puntuacion;

    @Column(name = "COMENTARIO")
    private String comentario;

    @Column(name = "FECHA_REGISTRO")
    private LocalDateTime fechaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO")
    private EstadoResena estado;
}
