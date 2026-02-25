package com.koroFoods.qualificationService.controller;

import com.koroFoods.qualificationService.dto.ResultadoResponse;
import com.koroFoods.qualificationService.dto.response.GraficoSeisList;
import com.koroFoods.qualificationService.service.CalificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/calificacion/feign")
public class CalificacionFeignController {

    private final CalificacionService service;


    @GetMapping("/graficoSeis")
    public ResponseEntity<ResultadoResponse<List<GraficoSeisList>>> graficoSeisList(@RequestParam("mes")Integer mes){
        ResultadoResponse<List<GraficoSeisList>> response = service.graficoSeisList(mes);
        if (response.isValor()) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
