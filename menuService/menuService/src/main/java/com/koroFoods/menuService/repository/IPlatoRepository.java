package com.koroFoods.menuService.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.koroFoods.menuService.enums.TipoPlato;
import com.koroFoods.menuService.model.Plato;

@Repository
public interface IPlatoRepository extends JpaRepository<Plato, Integer>{

	 List<Plato> findByActivoTrue();

	    Optional<Plato> findByIdPlatoAndActivoTrue(Integer id);

	    List<Plato> findByTipoPlatoAndActivoTrue(TipoPlato tipoPlato);

	    @Query("""
	        SELECT p FROM Plato p
	        WHERE p.activo = true
	        ORDER BY
	            CASE p.tipoPlato
	                WHEN 'E' THEN 1
	                WHEN 'S' THEN 2
	                WHEN 'P' THEN 3
	                WHEN 'B' THEN 4
	            END
	    """)
	    List<Plato> findAllActivosOrdenados();
}
