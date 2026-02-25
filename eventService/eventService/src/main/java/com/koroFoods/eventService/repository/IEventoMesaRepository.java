package com.koroFoods.eventService.repository;

import com.koroFoods.eventService.model.EventoMesa;

import feign.Param;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IEventoMesaRepository extends JpaRepository<EventoMesa, Integer> {

	List<EventoMesa> findByActivoTrue();

	List<EventoMesa> findByEvento_IdEventoAndActivoTrue(Integer idEvento);

	List<EventoMesa> findByIdMesaAndActivoTrue(Integer idMesa);

	@Query("SELECT COUNT(em) > 0 FROM EventoMesa em WHERE em.idMesa = :idMesa " + "AND em.activo = true "
			+ "AND em.idEventoMesa != :idEventoMesa "
			+ "AND ((em.fechaDesde <= :fechaHasta AND em.fechaHasta >= :fechaDesde))")
	boolean existeSolapamientoMesa(@Param("idMesa") Integer idMesa, @Param("fechaDesde") LocalDateTime fechaDesde,
			@Param("fechaHasta") LocalDateTime fechaHasta, @Param("idEventoMesa") Integer idEventoMesa);

	@Query("SELECT COUNT(em) > 0 FROM EventoMesa em WHERE em.idMesa = :idMesa " + "AND em.activo = true "
			+ "AND ((em.fechaDesde <= :fechaHasta AND em.fechaHasta >= :fechaDesde))")
	boolean existeSolapamientoMesaNuevo(@Param("idMesa") Integer idMesa, @Param("fechaDesde") LocalDateTime fechaDesde,
			@Param("fechaHasta") LocalDateTime fechaHasta);

	@Query("""
			    SELECT COUNT(em) > 0
			    FROM EventoMesa em
			    WHERE em.idMesa = :idMesa
			      AND em.evento.idEvento = :idEvento
			      AND em.activo = true
			      AND em.fechaDesde <= :fechaHasta
			      AND em.fechaHasta >= :fechaDesde
			""")
	boolean mesaAsignadaAlEvento(@Param("idMesa") Integer idMesa, @Param("idEvento") Integer idEvento,
			@Param("fechaDesde") LocalDateTime fechaDesde, @Param("fechaHasta") LocalDateTime fechaHasta);
}
