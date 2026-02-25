package com.koroFoods.menuService.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.koroFoods.menuService.model.Etiqueta;

@Repository
public interface IEtiquetaRepository extends JpaRepository<Etiqueta, Integer> {

	List<Etiqueta> findByActivo(Boolean activo);

    Optional<Etiqueta> findByIdEtiquetaAndActivo(Integer id, Boolean activo);

    boolean existsByNombreAndActivo(String nombre, Boolean activo);
}
