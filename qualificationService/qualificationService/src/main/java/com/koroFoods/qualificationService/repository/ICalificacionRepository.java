package com.koroFoods.qualificationService.repository;

import com.koroFoods.qualificationService.dto.response.GraficoSeisData;
import com.koroFoods.qualificationService.enums.TipoEntidad;
import com.koroFoods.qualificationService.model.Calificacion;

import java.util.List;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ICalificacionRepository extends JpaRepository<Calificacion, Integer> {
    List<Calificacion> findByIdUsuario(Integer idUsuario);

    boolean existsByIdUsuarioAndTipoEntidadAndIdEntidad(
            Integer idUsuario,
            TipoEntidad tipoEntidad,
            Integer idEntidad
    );

    @Query(value = """
            SELECT id_entidad as idEntidad,
             ROUND(AVG(puntuacion),2) promedio,
            COUNT(*) total
            FROM tb_calificacion
            WHERE DATE_PART('month',fecha_registro) = :mes
            AND tipo_entidad = 'PLATO' AND estado = 'ACT'
            GROUP BY id_entidad
            ORDER BY promedio DESC;
            """, nativeQuery = true)
    List<GraficoSeisData> graficoSeisList(@Param("mes") Integer mes);
}
