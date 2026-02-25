package com.koroFoods.tableService.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.koroFoods.tableService.dto.MesaRequest;
import com.koroFoods.tableService.dto.MesaResponse;
import com.koroFoods.tableService.enums.EstadoMesa;
import com.koroFoods.tableService.enums.Zona;
import com.koroFoods.tableService.exception.BusinessException;
import com.koroFoods.tableService.exception.ResourceNotFoundException;
import com.koroFoods.tableService.model.Mesa;
import com.koroFoods.tableService.repository.IMesaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MesaAdminService {

	private final IMesaRepository mesaRepository;

    @Transactional
    public MesaResponse crear(MesaRequest request) {
        validarZona(request.getZona());
        validarEstado(request.getEstado());

        // Verificar que no exista una mesa con el mismo número
        if (mesaRepository.existsByNumeroMesaAndActivoTrue(request.getNumeroMesa())) {
            throw new BusinessException("Ya existe una mesa activa con el número: " + request.getNumeroMesa());
        }

        Mesa mesa = new Mesa();
        mesa.setNumeroMesa(request.getNumeroMesa());
        mesa.setCapacidad(request.getCapacidad());
        mesa.setZona(Zona.valueOf(request.getZona()));
        mesa.setEstado(EstadoMesa.valueOf(request.getEstado()));
        mesa.setActivo(true);

        Mesa guardada = mesaRepository.save(mesa);
        return mapearAResponse(guardada);
    }

    @Transactional(readOnly = true)
    public List<MesaResponse> listarTodas() {
        return mesaRepository.findAll().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MesaResponse> listarActivas() {
        return mesaRepository.findByActivoTrue().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MesaResponse> listarPorZona(String zona) {
        validarZona(zona);
        return mesaRepository.findByZonaAndActivoTrue(Zona.valueOf(zona)).stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MesaResponse> listarPorEstado(String estado) {
        validarEstado(estado);
        return mesaRepository.findByEstadoAndActivoTrue(EstadoMesa.valueOf(estado)).stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MesaResponse buscarPorId(Integer id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con ID: " + id));
        return mapearAResponse(mesa);
    }

    @Transactional
    public MesaResponse actualizar(Integer id, MesaRequest request) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con ID: " + id));

        validarZona(request.getZona());
        validarEstado(request.getEstado());

        // Verificar que no exista otra mesa con el mismo número
        if (!mesa.getNumeroMesa().equals(request.getNumeroMesa()) &&
            mesaRepository.existsByNumeroMesaAndActivoTrue(request.getNumeroMesa())) {
            throw new BusinessException("Ya existe una mesa activa con el número: " + request.getNumeroMesa());
        }

        mesa.setNumeroMesa(request.getNumeroMesa());
        mesa.setCapacidad(request.getCapacidad());
        mesa.setZona(Zona.valueOf(request.getZona()));
        mesa.setEstado(EstadoMesa.valueOf(request.getEstado()));

        Mesa actualizada = mesaRepository.save(mesa);
        return mapearAResponse(actualizada);
    }

    @Transactional
    public void eliminar(Integer id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con ID: " + id));

        mesa.setActivo(false);
        mesaRepository.save(mesa);
    }

    @Transactional
    public MesaResponse cambiarEstado(Integer id, String nuevoEstado) {
        validarEstado(nuevoEstado);
        
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con ID: " + id));

        mesa.setEstado(EstadoMesa.valueOf(nuevoEstado));
        Mesa actualizada = mesaRepository.save(mesa);
        
        return mapearAResponse(actualizada);
    }

    private void validarZona(String zona) {
        try {
            Zona.valueOf(zona);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Zona inválida. Valores permitidos: Z1, Z2");
        }
    }

    private void validarEstado(String estado) {
        try {
            EstadoMesa.valueOf(estado);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Estado inválido. Valores permitidos: LIBRE, ASIGNADA, OCUPADA");
        }
    }

    private String obtenerDescripcionZona(Zona zona) {
        return switch (zona) {
            case Z1 -> "Zona 1";
            case Z2 -> "Zona 2";
        };
    }

    private String obtenerDescripcionEstado(EstadoMesa estado) {
        return switch (estado) {
            case LIBRE -> "Libre";
            case ASIGNADA -> "Asignada";
            case OCUPADA -> "Ocupada";
        };
    }

    private MesaResponse mapearAResponse(Mesa mesa) {
        return MesaResponse.builder()
                .idMesa(mesa.getIdMesa())
                .numeroMesa(mesa.getNumeroMesa())
                .capacidad(mesa.getCapacidad())
                .zona(mesa.getZona().name())
                .zonaDescripcion(obtenerDescripcionZona(mesa.getZona()))
                .estado(mesa.getEstado().name())
                .estadoDescripcion(obtenerDescripcionEstado(mesa.getEstado()))
                .activo(mesa.getActivo())
                .build();
    }
}
