package com.koroFoods.tableService.repository;

import com.koroFoods.tableService.enums.EstadoMesa;
import com.koroFoods.tableService.enums.Zona;
import com.koroFoods.tableService.model.Mesa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface IMesaRepository extends JpaRepository<Mesa, Integer> {

	List<Mesa> findByZonaAndActivoTrue(Zona zona);

	List<Mesa> findByActivoTrue();

	Optional<Mesa> findByIdMesaAndActivoTrue(Integer id);

	List<Mesa> findByEstadoAndActivoTrue(EstadoMesa estado);

	boolean existsByNumeroMesaAndActivoTrue(Integer numeroMesa);

	Optional<Mesa> findByNumeroMesaAndActivoTrue(Integer numeroMesa);

}
