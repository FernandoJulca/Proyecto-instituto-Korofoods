package com.koroFoods.menuService.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.koroFoods.menuService.dto.EtiquetaResponse;
import com.koroFoods.menuService.dto.PlatoRequest;
import com.koroFoods.menuService.dto.PlatoResponse;
import com.koroFoods.menuService.enums.TipoPlato;
import com.koroFoods.menuService.exception.BusinessException;
import com.koroFoods.menuService.exception.ResourceNotFoundException;
import com.koroFoods.menuService.model.Plato;
import com.koroFoods.menuService.repository.IPlatoEtiquetaRepository;
import com.koroFoods.menuService.repository.IPlatoRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlatoAdminService {

	private final IPlatoRepository platoRepository;
    private final IPlatoEtiquetaRepository platoEtiquetaRepository;
    private final CloudinaryService cloudinaryService;
    
    @Transactional
    public PlatoResponse crear(PlatoRequest request) {
        validarTipoPlato(request.getTipoPlato());

        Plato plato = new Plato();
        plato.setNombre(request.getNombre());
        plato.setPrecio(request.getPrecio());
        plato.setStock(request.getStock());
        plato.setTipoPlato(TipoPlato.valueOf(request.getTipoPlato()));
        plato.setActivo(true);

        // ✅ SUBIR IMAGEN A CLOUDINARY SI SE PROPORCIONÓ
        if (request.getImagenBase64() != null && !request.getImagenBase64().isBlank()) {
            try {
                String publicId = "plato_" + System.currentTimeMillis();
                String urlImagen = cloudinaryService.subirImagen(
                    request.getImagenBase64(),
                    "korofood/platos",
                    publicId
                );
                plato.setImagen(urlImagen);
                log.info("✅ Imagen subida para nuevo plato: {}", urlImagen);
            } catch (IOException e) {
                log.error("❌ Error al subir imagen", e);
                throw new BusinessException("Error al subir la imagen: " + e.getMessage());
            }
        } else if (request.getImagen() != null) {
            // Si viene una URL directa (para compatibilidad)
            plato.setImagen(request.getImagen());
        }

        Plato guardado = platoRepository.save(plato);
        return mapearAResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<PlatoResponse> listarTodos() {
        return platoRepository.findAll().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlatoResponse> listarActivos() {
        return platoRepository.findByActivoTrue().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlatoResponse> listarActivosOrdenados() {
        return platoRepository.findAllActivosOrdenados().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlatoResponse> listarPorTipo(String tipoPlato) {
        validarTipoPlato(tipoPlato);
        return platoRepository.findByTipoPlatoAndActivoTrue(TipoPlato.valueOf(tipoPlato)).stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlatoResponse buscarPorId(Integer id) {
        Plato plato = platoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plato no encontrado con ID: " + id));
        return mapearAResponse(plato);
    }

    @Transactional
    public PlatoResponse actualizar(Integer id, PlatoRequest request) {
        Plato plato = platoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plato no encontrado con ID: " + id));

        validarTipoPlato(request.getTipoPlato());

        plato.setNombre(request.getNombre());
        plato.setPrecio(request.getPrecio());
        plato.setStock(request.getStock());
        plato.setTipoPlato(TipoPlato.valueOf(request.getTipoPlato()));

        // ✅ ACTUALIZAR IMAGEN SI CAMBIÓ
        if (request.getImagenBase64() != null && !request.getImagenBase64().isBlank()) {
            try {
                // Eliminar imagen anterior si existe
                if (plato.getImagen() != null && plato.getImagen().contains("cloudinary.com")) {
                    String oldPublicId = cloudinaryService.extraerPublicId(plato.getImagen());
                    if (oldPublicId != null) {
                        cloudinaryService.eliminarImagen(oldPublicId);
                    }
                }

                // Subir nueva imagen
                String publicId = "plato_" + id;
                String urlImagen = cloudinaryService.subirImagen(
                    request.getImagenBase64(),
                    "korofood/platos",
                    publicId
                );
                plato.setImagen(urlImagen);
                log.info("✅ Imagen actualizada para plato {}: {}", id, urlImagen);
            } catch (IOException e) {
                log.error("❌ Error al actualizar imagen", e);
                throw new BusinessException("Error al actualizar la imagen: " + e.getMessage());
            }
        } else if (request.getImagen() != null && !request.getImagen().equals(plato.getImagen())) {
            // Si viene una URL diferente
            plato.setImagen(request.getImagen());
        }

        Plato actualizado = platoRepository.save(plato);
        return mapearAResponse(actualizado);
    }

    @Transactional
    public void eliminar(Integer id) {
        Plato plato = platoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plato no encontrado con ID: " + id));

        // ✅ OPCIONAL: Eliminar imagen de Cloudinary
        if (plato.getImagen() != null && plato.getImagen().contains("cloudinary.com")) {
            try {
                String publicId = cloudinaryService.extraerPublicId(plato.getImagen());
                if (publicId != null) {
                    cloudinaryService.eliminarImagen(publicId);
                }
            } catch (IOException e) {
                log.warn("No se pudo eliminar imagen de Cloudinary", e);
                // No fallar el proceso si la imagen no se puede eliminar
            }
        }

        plato.setActivo(false);
        platoRepository.save(plato);
    }

    private void validarTipoPlato(String tipoPlato) {
        try {
            TipoPlato.valueOf(tipoPlato);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Tipo de plato inválido. Valores permitidos: E, S, P, B");
        }
    }

    private String obtenerDescripcionTipoPlato(TipoPlato tipo) {
        return switch (tipo) {
            case E -> "Entrada";
            case S -> "Segundo";
            case P -> "Postre";
            case B -> "Bebida";
        };
    }

    private PlatoResponse mapearAResponse(Plato plato) {
        // Obtener etiquetas del plato
    	List<EtiquetaResponse> etiquetas = platoEtiquetaRepository
                .findByPlato_IdPlatoAndActivo(plato.getIdPlato(), true)
                .stream()
                .map(pe -> EtiquetaResponse.builder()
                        .idEtiqueta(pe.getEtiqueta().getIdEtiqueta())
                        .nombre(pe.getEtiqueta().getNombre())
                        .descripcion(pe.getEtiqueta().getDescripcion())
                        .activo(pe.getEtiqueta().getActivo()) // Ahora es Boolean
                        .fechaRegistro(pe.getEtiqueta().getFechaRegistro())
                        .build())
                .collect(Collectors.toList());

        return PlatoResponse.builder()
                .idPlato(plato.getIdPlato())
                .nombre(plato.getNombre())
                .precio(plato.getPrecio())
                .stock(plato.getStock())
                .tipoPlato(plato.getTipoPlato().name())
                .tipoPlayoDescripcion(obtenerDescripcionTipoPlato(plato.getTipoPlato()))
                .imagen(plato.getImagen())
                .activo(plato.getActivo())
                .etiquetas(etiquetas)
                .build();
    }
}
