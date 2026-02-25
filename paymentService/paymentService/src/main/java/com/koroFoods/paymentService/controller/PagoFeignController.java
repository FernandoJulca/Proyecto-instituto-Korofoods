package com.koroFoods.paymentService.controller;

import com.koroFoods.paymentService.dtos.response.GraficoTresData;
import com.koroFoods.paymentService.dtos.response.ResultadoResponse;
import com.koroFoods.paymentService.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/pago/feign")
public class PagoFeignController {

    private final PagoService pagoService;

    @GetMapping("/graficoTres")
    public ResponseEntity<ResultadoResponse<GraficoTresData>> getGraficoTres(@RequestParam Integer mes){
        ResultadoResponse<GraficoTresData> resultado = pagoService.graficoTresList(mes);

        if (resultado.isValor()) {
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
        }
    }
}
