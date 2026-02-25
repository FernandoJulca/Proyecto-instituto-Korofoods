package com.koroFoods.orderService.repository;


import com.koroFoods.orderService.dto.VentasMesaProjection;
import com.koroFoods.orderService.dto.response.DetalleCantidadPedidos;
import com.koroFoods.orderService.dto.response.GraficoCincoData;
import com.koroFoods.orderService.enums.EstadoPedido;
import com.koroFoods.orderService.model.Pedido;
import feign.Param;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IPedidoRepository extends JpaRepository<Pedido, Integer> {
	@Query("""
			SELECT p FROM Pedido p
			WHERE (:estado IS NULL OR p.estado = :estado)
			  AND p.estado <> com.koroFoods.orderService.enums.EstadoPedido.PA
			""")
	List<Pedido> findByEstadoOpcional(@Param("estado") EstadoPedido estado);

	Pedido findByIdReserva(Integer idReserva);


    @Query(value = """
           select
            SUM(CASE WHEN estado = 'PA' THEN 1 ELSE 0 END )as pedidosCompletados,
            COUNT(id_pedido) as pedidosTotales,
            SUM(CASE WHEN id_reserva IS NOT NULL THEN 1 ELSE 0 END) as clientesTotales
            from tb_pedido
            where id_usuario = :idUsuario
            """,nativeQuery = true)
    DetalleCantidadPedidos obtenerCantidadDePedidos(Integer idUsuario);



    List<Pedido>findByIdReservaIn(List<Integer> idReserva);



    @Query(value = """
            select
            id_usuario as idUsuario,
            COUNT(id_pedido) as completado
            FROM tb_pedido
            where DATE_PART('month',fecha_hora) = :mes
            AND estado = 'PA'
            group by id_usuario
            order by completado DESC
            """,nativeQuery = true)
    List<GraficoCincoData> graficoCincoList(@Param("mes")Integer mes);
    
    
    @Query(value = """
    	    SELECT DATE(p.FECHA_HORA)    AS "fecha",
    	           p.ID_MESA             AS "idMesa",
    	           COUNT(p.ID_PEDIDO)    AS "totalPedidos",
    	           SUM(p.TOTAL)          AS "totalVentas"
    	    FROM TB_PEDIDO p
    	    WHERE (CAST(:fechaInicio AS timestamp) IS NULL OR p.FECHA_HORA >= CAST(:fechaInicio AS timestamp))
    	      AND (CAST(:fechaFin    AS timestamp) IS NULL OR p.FECHA_HORA <= CAST(:fechaFin    AS timestamp))
    	      AND (CAST(:idMesa      AS integer)   IS NULL OR p.ID_MESA     = CAST(:idMesa      AS integer))
    	    GROUP BY DATE(p.FECHA_HORA), p.ID_MESA
    	    ORDER BY DATE(p.FECHA_HORA) DESC, p.ID_MESA
    	    """, nativeQuery = true)
    	List<VentasMesaProjection> ventasPorFechaMesa(
    	    @Param("fechaInicio") LocalDateTime fechaInicio,
    	    @Param("fechaFin")    LocalDateTime fechaFin,
    	    @Param("idMesa")      Integer idMesa
    	);
   
}
