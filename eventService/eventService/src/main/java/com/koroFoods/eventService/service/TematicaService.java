package com.koroFoods.eventService.service;

import com.koroFoods.eventService.dtos.TematicRequest;
import com.koroFoods.eventService.dtos.TematicResponse;
import com.koroFoods.eventService.exception.BusinessException;
import com.koroFoods.eventService.exception.ResourceNotFoundException;
import com.koroFoods.eventService.model.Tematica;
import com.koroFoods.eventService.repository.ITematicaRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TematicaService {

    private final ITematicaRepository tematicaRepository;
    
    @Transactional
    public TematicResponse crear(TematicRequest request) {
        if (tematicaRepository.existsByNombreAndActivoTrue(request.getNombre())) {
            throw new BusinessException("Ya existe una temática con el nombre: " + request.getNombre());
        }

        Tematica tematica = new Tematica();
        tematica.setNombre(request.getNombre());
        tematica.setActivo(true);

        Tematica guardada = tematicaRepository.save(tematica);
        return mapearAResponse(guardada);
    }

    @Transactional(readOnly = true)
    public List<TematicResponse> listarTodas() {
        return tematicaRepository.findAll().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TematicResponse> listarActivas() {
        return tematicaRepository.findByActivoTrue().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TematicResponse buscarPorId(Integer id) {
        Tematica tematica = tematicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Temática no encontrada con ID: " + id));
        return mapearAResponse(tematica);
    }

    @Transactional
    public TematicResponse actualizar(Integer id, TematicRequest request) {
        Tematica tematica = tematicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Temática no encontrada con ID: " + id));

        if (!tematica.getNombre().equals(request.getNombre()) &&
            tematicaRepository.existsByNombreAndActivoTrue(request.getNombre())) {
            throw new BusinessException("Ya existe una temática con el nombre: " + request.getNombre());
        }

        tematica.setNombre(request.getNombre());
        Tematica actualizada = tematicaRepository.save(tematica);
        return mapearAResponse(actualizada);
    }

    @Transactional
    public void eliminar(Integer id) {
        Tematica tematica = tematicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Temática no encontrada con ID: " + id));
        
        tematica.setActivo(false);
        tematicaRepository.save(tematica);
    }

    private TematicResponse mapearAResponse(Tematica tematica) {
        return TematicResponse.builder()
                .idTematica(tematica.getIdTematica())
                .nombre(tematica.getNombre())
                .activo(tematica.getActivo())
                .build();
    }
}
