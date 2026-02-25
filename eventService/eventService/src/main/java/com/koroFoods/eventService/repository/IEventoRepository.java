package com.koroFoods.eventService.repository;

import com.koroFoods.eventService.model.Evento;

import feign.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IEventoRepository extends JpaRepository<Evento, Integer> {

	List<Evento> findByActivoTrue();

	Optional<Evento> findByIdEventoAndActivoTrue(Integer id);

	List<Evento> findByTematica_IdTematicaAndActivoTrue(Integer idTematica);

	@Query("SELECT e FROM Evento e WHERE e.fechaInicio >= :fechaActual AND e.activo = true")
	List<Evento> findEventosFuturos(LocalDateTime fechaActual);

	@Query("""
			    SELECT e FROM Evento e
			    WHERE e.activo = true
			    AND e.fechaInicio >= :inicio
			    AND e.fechaFin < :fin
			""")
	List<Evento> findEventosDelDia(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
