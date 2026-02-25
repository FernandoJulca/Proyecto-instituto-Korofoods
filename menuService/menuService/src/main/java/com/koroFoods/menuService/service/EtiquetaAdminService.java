package com.koroFoods.menuService.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.koroFoods.menuService.dto.EtiquetaRequest;
import com.koroFoods.menuService.dto.EtiquetaResponse;
import com.koroFoods.menuService.exception.BusinessException;
import com.koroFoods.menuService.exception.ResourceNotFoundException;
import com.koroFoods.menuService.model.Etiqueta;
import com.koroFoods.menuService.repository.IEtiquetaRepository;
import com.koroFoods.menuService.repository.IPlatoEtiquetaRepository;
import com.koroFoods.menuService.repository.IPlatoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EtiquetaAdminService {

	private final IEtiquetaRepository etiquetaRepository;

    @Transactional
    public EtiquetaResponse crear(EtiquetaRequest request) {
        if (etiquetaRepository.existsByNombreAndActivo(request.getNombre(), true)) {
            throw new BusinessException("Ya existe una etiqueta activa con el nombre: " + request.getNombre());
        }

        Etiqueta etiqueta = new Etiqueta();
        etiqueta.setNombre(request.getNombre());
        etiqueta.setDescripcion(request.getDescripcion());
        etiqueta.setActivo(true);
        etiqueta.setFechaRegistro(LocalDateTime.now());

        Etiqueta guardada = etiquetaRepository.save(etiqueta);
        return mapearAResponse(guardada);
    }

    @Transactional(readOnly = true)
    public List<EtiquetaResponse> listarTodas() {
        return etiquetaRepository.findAll().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EtiquetaResponse> listarActivas() {
        return etiquetaRepository.findByActivo(true).stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EtiquetaResponse buscarPorId(Integer id) {
        Etiqueta etiqueta = etiquetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada con ID: " + id));
        return mapearAResponse(etiqueta);
    }

    @Transactional
    public EtiquetaResponse actualizar(Integer id, EtiquetaRequest request) {
        Etiqueta etiqueta = etiquetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada con ID: " + id));

        if (!etiqueta.getNombre().equals(request.getNombre()) &&
            etiquetaRepository.existsByNombreAndActivo(request.getNombre(), true)) {
            throw new BusinessException("Ya existe una etiqueta activa con el nombre: " + request.getNombre());
        }

        etiqueta.setNombre(request.getNombre());
        etiqueta.setDescripcion(request.getDescripcion());

        Etiqueta actualizada = etiquetaRepository.save(etiqueta);
        return mapearAResponse(actualizada);
    }

    @Transactional
    public void eliminar(Integer id) {
        Etiqueta etiqueta = etiquetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada con ID: " + id));

        etiqueta.setActivo(false);
        etiquetaRepository.save(etiqueta);
    }

    private EtiquetaResponse mapearAResponse(Etiqueta etiqueta) {
        return EtiquetaResponse.builder()
                .idEtiqueta(etiqueta.getIdEtiqueta())
                .nombre(etiqueta.getNombre())
                .descripcion(etiqueta.getDescripcion())
                .activo(etiqueta.getActivo())
                .fechaRegistro(etiqueta.getFechaRegistro())
                .build();
    }
}
