package com.koroFoods.userService.controller;

import com.koroFoods.userService.model.Distrito;
import com.koroFoods.userService.service.DistritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/distrito")
@RequiredArgsConstructor
public class DistritoController {

    private final DistritoService distritoService;

    @GetMapping("/list")
    public ResponseEntity<?>lista(){
        List<Distrito> lista = distritoService.listaDeDistritos();

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> findId(@PathVariable Integer id){
        Distrito distrito = distritoService.obtenerDistritoId(id);

        return ResponseEntity.ok(distrito);
    }
}
