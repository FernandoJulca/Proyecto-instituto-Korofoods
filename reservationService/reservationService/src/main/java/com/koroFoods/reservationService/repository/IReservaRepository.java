package com.koroFoods.reservationService.repository;

import com.koroFoods.reservationService.dto.response.Grafico2Data;
import com.koroFoods.reservationService.dto.response.GraficoCuatroData;
import com.koroFoods.reservationService.model.Reserva;

import feign.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IReservaRepository extends JpaRepository<Reserva, Integer> {

	@Query("""
			    SELECT r FROM Reserva r
			    WHERE r.estado = 'ASISTIDA'
			      AND r.codigoVerificacion = :codigo
			""")
	Optional<Reserva> findReservaAsistidaById(@Param("codigo") String codigo);

	@Query(value = """
			    SELECT COUNT(*) > 0
			    FROM TB_RESERVA r
			    WHERE r.ID_MESA = :idMesa
			      AND r.ESTADO IN ('PAGADA', 'ASISTIDA')
			      AND r.FECHA_RESERVA < :fechaHasta
			      AND (
			            CASE
			                WHEN r.ID_EVENTO IS NOT NULL
			                    THEN r.FECHA_RESERVA + INTERVAL '3 hour'
			                ELSE
			                    r.FECHA_RESERVA + INTERVAL '2 hour'
			            END
			          ) > :fechaDesde
			""", nativeQuery = true)
	boolean existeSolapamientoReserva(@Param("idMesa") Integer idMesa, @Param("fechaDesde") LocalDateTime fechaDesde,
			@Param("fechaHasta") LocalDateTime fechaHasta);

	List<Reserva> findByIdUsuario(Integer idUsuario);

	@Query("""
			    SELECT r FROM Reserva r
			    WHERE r.estado = 'PAGADA'
			    AND r.idReserva = :idReserva
			""")
	Optional<Reserva> findReservaPagadaById(@Param("idReserva") Integer idReserva);

	@Query(value = """
			SELECT r.ID_MESA
			FROM TB_RESERVA r
			WHERE r.ID_MESA IN (:idsMesas)
			  AND r.ESTADO IN ('PAGADA', 'ASISTIDA')
			  AND r.FECHA_RESERVA < :fechaHasta
			  AND (
			        CASE
			            WHEN r.ID_EVENTO IS NOT NULL
			                THEN r.FECHA_RESERVA + INTERVAL '3 hour'
			            ELSE
			                r.FECHA_RESERVA + INTERVAL '2 hour'
			        END
			      ) > :fechaDesde
			""", nativeQuery = true)
	List<Integer> findMesasOcupadasEnRango(@Param("idsMesas") List<Integer> idsMesas,
			@Param("fechaDesde") LocalDateTime fechaDesde, @Param("fechaHasta") LocalDateTime fechaHasta);

	@Query("""
			    SELECT r FROM Reserva r
			    WHERE r.estado IN ('PENDIENTE', 'PAGADA')
			    AND r.fechaHora <= :limiteTolerancia
			""")
	List<Reserva> findReservasVencidas(@Param("limiteTolerancia") LocalDateTime limiteTolerancia);

	// COUNTS - Dashboard Recepcionista

	@Query("""
			    SELECT COUNT(r) FROM Reserva r
			    WHERE r.estado IN ('PAGADA', 'ASISTIDA')
			    AND r.fechaHora >= :inicio
			    AND r.fechaHora < :fin
			""")
	long countReservasTotalesDelDia(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

	@Query("""
			    SELECT COUNT(r) FROM Reserva r
			    WHERE r.estado = 'ASISTIDA'
			    AND r.fechaHora >= :inicio
			    AND r.fechaHora < :fin
			""")
	long countReservasAsistidas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

	@Query("""
			    SELECT r FROM Reserva r
			    WHERE r.estado = 'ASISTIDA'
			    AND r.fechaHora >= :inicio
			    AND r.fechaHora < :fin
			    ORDER BY r.fechaHora DESC
			""")
	List<Reserva> findReservasAsistidasPorFecha(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

	@Query(value = """
			SELECT
			SUM(CASE WHEN id_evento IS NOT NULL THEN 1 ELSE 0 END) as conEvento,
			SUM(CASE WHEN id_evento IS NULL THEN 1 ELSE 0 END) as sinEvento
			FROM tb_reserva
			WHERE DATE_PART('month', fecha_registro) = :mes
			""", nativeQuery = true)
	Grafico2Data graficoDosList(@Param("mes") Integer mes);

	@Query(value = """
			select
			id_evento as idEvento,
			count(id_evento) as cantidad
			from tb_reserva
			where DATE_PART('month',fecha_registro) = :mes
			and id_evento IS NOT NULL
			group by id_evento
			order by cantidad desc
			""", nativeQuery = true)
	List<GraficoCuatroData> graficoCuatroList(@Param("mes") Integer mes);

    
	@Query("SELECT r FROM Reserva r WHERE " +
		       "r.fechaHora BETWEEN :fechaInicio AND :fechaFin " +
		       "AND (:estado IS NULL OR r.estado = :estado) " +
		       "ORDER BY r.fechaHora DESC")
		List<Reserva> findReservasParaReporte(
		    @Param("fechaInicio") LocalDateTime fechaInicio,
		    @Param("fechaFin") LocalDateTime fechaFin,
		    @Param("estado") String estado
		);
}
