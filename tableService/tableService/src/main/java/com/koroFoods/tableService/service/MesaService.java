package com.koroFoods.tableService.service;

import com.koroFoods.tableService.dto.MesaDtoFeign;
import com.koroFoods.tableService.dto.ResultadoResponse;
import com.koroFoods.tableService.enums.EstadoMesa;
import com.koroFoods.tableService.enums.Zona;
import com.koroFoods.tableService.model.Mesa;
import com.koroFoods.tableService.repository.IMesaRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MesaService {

	private final IMesaRepository mesaRepository;

    public ResultadoResponse<MesaDtoFeign> getTableById(Integer id){
        Mesa mesa = mesaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
        
        MesaDtoFeign dto = convertirAMesaFeign(mesa);
        return ResultadoResponse.success("Mesa encontrada", dto);
    }

    public ResultadoResponse<List<MesaDtoFeign>> obtenerMesasPorZona(
            Zona zona, 
            Integer cantidadPersonas) {

        Integer capacidadRequerida = null;
        if (cantidadPersonas != null) {
            capacidadRequerida = calcularCapacidadPar(cantidadPersonas);
        }
        
        final Integer capacidadFinal = capacidadRequerida;

        List<MesaDtoFeign> mesas = mesaRepository.findByZonaAndActivoTrue(zona)
                .stream()
                .filter(mesa -> {
                    if (capacidadFinal != null) {
                        return Integer.valueOf(mesa.getCapacidad()).equals(capacidadFinal);
                    }
                    return true;
                })
                .map(this::convertirAMesaFeign)
                .toList();

        String mensaje = cantidadPersonas != null 
                ? String.format("Mesas encontradas en zona %s con capacidad para %d personas", 
                        zona, cantidadPersonas)
                : "Mesas encontradas en zona " + zona;

        return ResultadoResponse.success(mensaje, mesas);
    }

    private Integer calcularCapacidadPar(Integer cantidadPersonas) {
        if (cantidadPersonas == null || cantidadPersonas <= 0) {
            return null;
        }
        
        if (cantidadPersonas % 2 == 0) {
            return cantidadPersonas;
        }
        
        return cantidadPersonas + 1;
    }

    public ResultadoResponse<List<MesaDtoFeign>> obtenerMesasPorIds(List<Integer> ids) {
        List<MesaDtoFeign> mesas = mesaRepository.findAllById(ids)
                .stream()
                .map(this::convertirAMesaFeign)
                .collect(Collectors.toList());
        
        return ResultadoResponse.success("Mesas encontradas", mesas);
    }
    
    public ResultadoResponse<MesaDtoFeign> cambiarEstadoOcupada(Integer idMesa) {
    	Mesa m = mesaRepository.findById(idMesa).orElse(null);
    	m.setEstado(EstadoMesa.OCUPADA);
    	mesaRepository.save(m);
    	
    	MesaDtoFeign mesita = convertirAMesaFeign(m);
    	return ResultadoResponse.success("Se cambio el estado de la mesa a ocupada", mesita);
    }
    
    private MesaDtoFeign convertirAMesaFeign(Mesa mesa) {
        MesaDtoFeign dto = new MesaDtoFeign();
        dto.setIdMesa(mesa.getIdMesa());
        dto.setNumeroMesa(mesa.getNumeroMesa());
        dto.setCapacidad(mesa.getCapacidad());
        dto.setTipo(mesa.getZona().toString());
        dto.setEstado(mesa.getEstado().toString());
        return dto;
    }
}
