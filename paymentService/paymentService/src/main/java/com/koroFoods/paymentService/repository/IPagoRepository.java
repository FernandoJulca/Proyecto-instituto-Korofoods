package com.koroFoods.paymentService.repository;

import com.koroFoods.paymentService.dtos.response.GraficoTresData;
import com.koroFoods.paymentService.enums.EstadoPago;
import com.koroFoods.paymentService.model.Pago;

import java.util.List;
import java.util.Optional;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IPagoRepository extends JpaRepository<Pago, Integer> {

    Optional<Pago> findByReferenciaPago(String referenciaPago);

    List<Pago> findByIdUsuario(Integer idUsuario);

    List<Pago> findByIdReserva(Integer idReserva);

    List<Pago> findByIdPedido(Integer idPedido);

    List<Pago> findByEstado(EstadoPago estado);


    boolean existsByHashImagen(String hashImagen);

    boolean existsByCodigoOperacion(String codigoOperacion);

    Optional<Pago> findByHashImagen(String hashImagen);

    Optional<Pago> findByCodigoOperacion(String codigoOperacion);

    @Query(value = """
            SELECT
            SUM(CASE WHEN metodo_pago = 'YAPE' then 1 else 0 end) as pagoYape,
            SUM(CASE WHEN metodo_pago = 'TARJETA' then 1 else 0 end) as pagoTarjeta,
            SUM(CASE WHEN metodo_pago = 'EFECTIVO' then 1 else 0 end) as pagoEfectivo,
            SUM(CASE WHEN metodo_pago = 'PLIN' then 1 else 0 end) as pagoPlin
            FROM tb_pago
            WHERE DATE_PART('month', fecha_creacion) = :mes
            """, nativeQuery = true)
    GraficoTresData graficoTresList(@Param("mes") Integer mes);
}