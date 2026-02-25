package com.koroFoods.orderService.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.koroFoods.orderService.dto.PlatosMasVendidosDTO;
import com.koroFoods.orderService.dto.PlatosMasVendidosProjection;
import com.koroFoods.orderService.dto.ResultadoResponse;
import com.koroFoods.orderService.dto.VentasMesaProjection;
import com.koroFoods.orderService.dto.VentasPorFechaMesaDTO;
import com.koroFoods.orderService.feign.MesaFeignClient;
import com.koroFoods.orderService.feign.PlatoFeignClient;
import com.koroFoods.orderService.repository.IDetallePedidoRepository;
import com.koroFoods.orderService.repository.IPedidoRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReporteService {


    private final IPedidoRepository pedidoRepository;
    private final IDetallePedidoRepository detallePedidoRepository;
    private final MesaFeignClient mesaFeignClient;
    private final PlatoFeignClient platoFeignClient;

    // HU22 – Ventas por fecha y mesa
    public ResultadoResponse<List<VentasPorFechaMesaDTO>> ventasPorFechaMesa(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Integer idMesa) {

        LocalDateTime inicio = fechaInicio != null
                ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = fechaFin != null
                ? fechaFin.atTime(23, 59, 59) : null;

        List<VentasMesaProjection> raw =
                pedidoRepository.ventasPorFechaMesa(inicio, fin, idMesa);

        if (raw.isEmpty()) {
            return ResultadoResponse.success("Sin datos para el rango indicado", List.of());
        }

        List<VentasPorFechaMesaDTO> resultado = raw.stream().map((VentasMesaProjection r)-> {
            VentasPorFechaMesaDTO dto = new VentasPorFechaMesaDTO();
            dto.setFecha(r.getFecha());
            dto.setIdMesa(r.getIdMesa());
            dto.setTotalPedidos(r.getTotalPedidos());
            dto.setTotalVentas(r.getTotalVentas());

            // Enriquecer con datos de la mesa via Feign
            try {
                var mesaResp = mesaFeignClient.getTableById(r.getIdMesa());
                if (mesaResp != null && mesaResp.getData() != null) {
                    dto.setNumeroMesa(mesaResp.getData().getNumeroMesa());
                    dto.setZona(mesaResp.getData().getTipo()); // tipo = zona en MesaDtoFeign
                }
            } catch (Exception e) {
                log.warn("No se pudo obtener info de mesa {}: {}", r.getIdMesa(), e.getMessage());
                dto.setNumeroMesa(r.getIdMesa());
                dto.setZona("N/A");
            }
            return dto;
        }).toList();

        return ResultadoResponse.success("Reporte obtenido", resultado);
    }

    // HU23 – Platos más vendidos
    public ResultadoResponse<List<PlatosMasVendidosDTO>> platosMasVendidos(
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        LocalDateTime inicio = fechaInicio != null
                ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = fechaFin != null
                ? fechaFin.atTime(23, 59, 59) : null;

        List<PlatosMasVendidosProjection> raw =
                detallePedidoRepository.platosMasVendidos(inicio, fin);

        if (raw.isEmpty()) {
            return ResultadoResponse.success("Sin datos para el rango indicado", List.of());
        }

        List<PlatosMasVendidosDTO> resultado = raw.stream().map((PlatosMasVendidosProjection  r) -> {
        	PlatosMasVendidosDTO dto = new PlatosMasVendidosDTO();
            dto.setIdPlato(r.getIdPlato());
            dto.setCantidadVendida(r.getCantidadVendida());
            dto.setTotalGenerado(r.getTotalGenerado());

            // Enriquecer con datos del plato via Feign
            try {
                var platoResp = platoFeignClient.getDishById(r.getIdPlato());
                if (platoResp != null && platoResp.getData() != null) {
                    dto.setNombrePlato(platoResp.getData().getNombre());
                    dto.setTipoPlato(platoResp.getData().getTipoPlato());
                }
            } catch (Exception e) {
                log.warn("No se pudo obtener info del plato {}: {}", r.getIdPlato(), e.getMessage());
                dto.setNombrePlato("Plato #" + r.getIdPlato());
                dto.setTipoPlato("N/A");
            }
            return dto;
        }).toList();

        return ResultadoResponse.success("Reporte obtenido", resultado);
    }
}
