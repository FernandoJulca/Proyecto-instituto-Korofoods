package com.koroFoods.menuService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.koroFoods.menuService.model.PlatoEtiqueta;

@Repository
public interface IPlatoEtiquetaRepository extends JpaRepository<PlatoEtiqueta, Integer>{

	List<PlatoEtiqueta> findByPlato_IdPlatoAndActivo(Integer idPlato, Boolean activo);

    @Modifying
    @Query("UPDATE PlatoEtiqueta pe SET pe.activo = false WHERE pe.plato.idPlato = :idPlato")
    void desactivarPorPlato(@Param("idPlato") Integer idPlato);
}
