package com.koroFoods.userService.service;

import com.koroFoods.userService.model.Distrito;
import com.koroFoods.userService.repository.IDistritoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DistritoService {

    private final IDistritoRepository distritoRepository;

    public List<Distrito> listaDeDistritos(){
        return distritoRepository.findAll();
    }

    public Distrito obtenerDistritoId(Integer id){
        return distritoRepository.findById(id).orElseThrow(
                () -> new RuntimeException("No se encontro distrito para el ID proporcionado: " + id)
        );
    }
}
