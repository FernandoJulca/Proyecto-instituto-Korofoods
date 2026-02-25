package com.koroFoods.eventService.repository;

import com.koroFoods.eventService.model.Tematica;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITematicaRepository extends JpaRepository<Tematica,Integer> {
	
	List<Tematica> findByActivoTrue();

    Optional<Tematica> findByIdTematicaAndActivoTrue(Integer id);

    boolean existsByNombreAndActivoTrue(String nombre);
}
