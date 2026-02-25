package com.koroFoods.menuService.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "TB_PLATO_ETIQUETAS")
public class PlatoEtiqueta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PLATO_ETIQUETA")
	private Integer idPlatoEtiqueta;
	

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PLATO", nullable = false)
    private Plato plato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ETIQUETA", nullable = false)
    private Etiqueta etiqueta;
    
    @Column(name="ACTIVO")
    private Boolean activo;

}
