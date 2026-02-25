package com.koroFoods.orderService.repository;


import com.koroFoods.orderService.dto.PlatosMasVendidosProjection;
import com.koroFoods.orderService.dto.response.*;
import com.koroFoods.orderService.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IDetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {


    @Query(value = """
            SELECT * 
            FROM tb_detalle_pedido
            WHERE id_pedido = :idPedido
            ORDER BY
              CASE estado
                WHEN 'PED' THEN 1
                WHEN 'ENT' THEN 2
                WHEN 'CAN' THEN 3
              END
            """,nativeQuery = true)
    List<DetallePedido> findByIdPedidoDescEstado(Integer idPedido);

    @Query(value = """
            select
            SUM(CASE WHEN estado = 'PED' then 1 else 0 END) as pedidos,
            SUM(CASE WHEN estado = 'ENT' then 1 else 0 END) as entregados,
            SUM(CASE WHEN estado = 'CAN' then 1 else 0 END) as cancelados
            from tb_detalle_pedido
            where id_pedido = :idPedido
            """,nativeQuery = true)
    DetalleEstadoCount findByIdPedido(Integer idPedido);


    @Query(value = """
            select
            dt.id_plato as idPlato,
            SUM(dt.cantidad) as cantidadPlatos
            from tb_detalle_pedido dt
            INNER JOIN tb_pedido p ON  dt.id_pedido = p.id_pedido
            where DATE_PART('month',p.fecha_hora) = :mes
            GROUP BY dt.id_plato
            Order by cantidadPlatos desc
            limit 10
            """,nativeQuery = true)
    List<GraficoUnoData> graficoUnoList(@Param("mes")Integer mes);
    
    @Query("""
    		SELECT d.idPlato as idPlato,
    		       SUM(d.cantidad) as totalCantidad
    		FROM DetallePedido d
    		JOIN Pedido p ON p.idPedido = d.idPedido
    		WHERE MONTH(p.fechaHora) = :mes
    		AND d.estado <> 'CAN'
    		GROUP BY d.idPlato
    		ORDER BY totalCantidad DESC
    		""")
    		List<PlatosMasVendidosProjection> platosMasVendidos(int mes);
    
    @Query(value = """
    	    SELECT dp.ID_PLATO          AS "idPlato",
    	           SUM(dp.CANTIDAD)     AS "cantidadVendida",
    	           SUM(dp.SUBTOTAL)     AS "totalGenerado"
    	    FROM TB_DETALLE_PEDIDO dp
    	    JOIN TB_PEDIDO p ON p.ID_PEDIDO = dp.ID_PEDIDO
    	    WHERE (CAST(:fechaInicio AS timestamp) IS NULL OR p.FECHA_HORA >= CAST(:fechaInicio AS timestamp))
    	      AND (CAST(:fechaFin    AS timestamp) IS NULL OR p.FECHA_HORA <= CAST(:fechaFin    AS timestamp))
    	    GROUP BY dp.ID_PLATO
    	    ORDER BY SUM(dp.CANTIDAD) DESC
    	    """, nativeQuery = true)
    	List<PlatosMasVendidosProjection> platosMasVendidos(
    	    @Param("fechaInicio") LocalDateTime fechaInicio,
    	    @Param("fechaFin")    LocalDateTime fechaFin
    	);
}
