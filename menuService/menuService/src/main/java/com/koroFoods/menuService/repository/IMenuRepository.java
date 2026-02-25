package com.koroFoods.menuService.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.koroFoods.menuService.model.Plato;

public interface IMenuRepository extends JpaRepository<Plato, Integer> {

	@Query("""
		    SELECT p
		    FROM Plato p
		    LEFT JOIN FETCH p.platoEtiquetas pe
		    LEFT JOIN FETCH pe.etiqueta e
		    WHERE p.activo = TRUE
		      AND (pe.activo = TRUE OR pe IS NULL)
		      AND (e.activo = TRUE OR e IS NULL)
		    ORDER BY
		        CASE p.tipoPlato
		            WHEN 'E' THEN 1
		            WHEN 'S' THEN 2
		            WHEN 'P' THEN 3
		            WHEN 'B' THEN 4
		        END
		""")
		List<Plato> findActivosConEtiquetasOrdenados();

}
