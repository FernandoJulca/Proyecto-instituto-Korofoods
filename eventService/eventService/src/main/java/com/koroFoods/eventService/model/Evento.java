package com.koroFoods.eventService.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_EVENTO")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EVENTO")
    private Integer idEvento;

    @Column(name = "NOMBRE")
    private String nombre;
    
    @Column(name = "DESCRIPCION")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "ID_TEMATICA")
    private Tematica tematica;

    @Column(name = "FECHA_INICIO")
    private LocalDateTime fechaInicio;
    
    @Column(name = "FECHA_FIN")
    private LocalDateTime fechaFin;

    @Column(name = "COSTO_EVENTO")
    private BigDecimal costo;

    @Column(name = "IMAGEN")
    private String imagen;
    
    @Column(name="ACTIVO")
    private Boolean activo;
    
    @JsonIgnore
    @Transient
    private MultipartFile imagenMultipart; // para la subida de imagens
}
