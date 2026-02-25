package com.koroFoods.menuService.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.koroFoods.menuService.enums.TipoPlato;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_PLATO")
public class Plato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_PLATO")
    private Integer idPlato;

    @Column(name="NOMBRE")
    private String nombre;

    @Column(name="PRECIO")
    private BigDecimal precio;

    @Column(name="STOCK")
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(name="TIPO_PLATO")
    private TipoPlato tipoPlato;

    @Column(name="IMAGEN")
    private String imagen;

    @Column(name="ACTIVO")
    private Boolean activo;

    @JsonIgnore
    @Transient
    private MultipartFile imagenMultipart; // para la subida de imagens
    
    @OneToMany(mappedBy = "plato", fetch = FetchType.LAZY)
    private List<PlatoEtiqueta> platoEtiquetas;
}
