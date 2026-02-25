package com.koroFoods.menuService.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.koroFoods.menuService.dto.PlatoEtiquetaRequest;
import com.koroFoods.menuService.exception.ResourceNotFoundException;
import com.koroFoods.menuService.model.Etiqueta;
import com.koroFoods.menuService.model.Plato;
import com.koroFoods.menuService.model.PlatoEtiqueta;
import com.koroFoods.menuService.repository.IEtiquetaRepository;
import com.koroFoods.menuService.repository.IPlatoEtiquetaRepository;
import com.koroFoods.menuService.repository.IPlatoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlatoEtiquetaAdminService {

	private final IPlatoRepository platoRepository;
    private final IEtiquetaRepository etiquetaRepository;
    private final IPlatoEtiquetaRepository platoEtiquetaRepository;

    @Transactional
    public void asignarEtiquetas(PlatoEtiquetaRequest request) {
        Plato plato = platoRepository.findById(request.getIdPlato())
                .orElseThrow(() -> new ResourceNotFoundException("Plato no encontrado con ID: " + request.getIdPlato()));

        // Desactivar etiquetas anteriores
        platoEtiquetaRepository.desactivarPorPlato(request.getIdPlato());

        // Asignar nuevas etiquetas
        for (Integer idEtiqueta : request.getIdsEtiquetas()) {
            Etiqueta etiqueta = etiquetaRepository.findById(idEtiqueta)
                    .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada con ID: " + idEtiqueta));

            PlatoEtiqueta platoEtiqueta = new PlatoEtiqueta();
            platoEtiqueta.setPlato(plato);
            platoEtiqueta.setEtiqueta(etiqueta);
            platoEtiqueta.setActivo(true);

            platoEtiquetaRepository.save(platoEtiqueta);
        }
    }
}
