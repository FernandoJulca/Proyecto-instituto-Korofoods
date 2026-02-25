package com.koroFoods.paymentService.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.koroFoods.paymentService.dtos.ConfirmarPagoRequest;
import com.koroFoods.paymentService.dtos.CrearPagoRequest;
import com.koroFoods.paymentService.dtos.PagoResponse;
import com.koroFoods.paymentService.dtos.QRDataResponse;
import com.koroFoods.paymentService.dtos.SubirCapturaRequest;
import com.koroFoods.paymentService.exception.BusinessException;
import com.koroFoods.paymentService.service.CloudinaryService;
import com.koroFoods.paymentService.service.GoogleVisionService;
import com.koroFoods.paymentService.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pago")
@RequiredArgsConstructor
public class PagoController {
	
	private final PagoService pagoService;
	private final CloudinaryService cloudinaryService;
	private final GoogleVisionService googleVisionService;

    /**
     * Crear un nuevo pago y obtener datos para generar QR
     */
    @PostMapping
    public ResponseEntity<QRDataResponse> crearPago(@Valid @RequestBody CrearPagoRequest request) {
        QRDataResponse response = pagoService.crearPago(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Confirmar un pago ingresando el código de operación
     */
    @PostMapping("/confirmar")
    public ResponseEntity<PagoResponse> confirmarPago(@Valid @RequestBody ConfirmarPagoRequest request) {
        PagoResponse response = pagoService.confirmarPago(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Anular un pago
     */
    @PatchMapping("/{id}/anular")
    public ResponseEntity<PagoResponse> anularPago(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        String motivo = body.getOrDefault("motivo", "Anulación solicitada por el usuario");
        PagoResponse response = pagoService.anularPago(id, motivo);
        return ResponseEntity.ok(response);
    }

    /**
     * Listar todos los pagos
     */
    @GetMapping
    public ResponseEntity<List<PagoResponse>> listarTodos() {
        List<PagoResponse> pagos = pagoService.listarTodos();
        return ResponseEntity.ok(pagos);
    }

    /**
     * Listar pagos por usuario
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<PagoResponse>> listarPorUsuario(@PathVariable Integer idUsuario) {
        List<PagoResponse> pagos = pagoService.listarPorUsuario(idUsuario);
        return ResponseEntity.ok(pagos);
    }

    /**
     * Buscar pago por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PagoResponse> buscarPorId(@PathVariable Integer id) {
        PagoResponse response = pagoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Buscar pago por referencia
     */
    @GetMapping("/referencia/{referencia}")
    public ResponseEntity<PagoResponse> buscarPorReferencia(@PathVariable String referencia) {
        PagoResponse response = pagoService.buscarPorReferencia(referencia);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/subir-captura")
    public ResponseEntity<PagoResponse> subirCaptura(@Valid @RequestBody SubirCapturaRequest request) {
        try {
            PagoResponse response = pagoService.subirCaptura(request);
            return ResponseEntity.ok(response);
        } catch (BusinessException e) {
            // El servicio ya guardó el estado RECHAZADO
            // Devolver el pago con el motivo de rechazo
            PagoResponse pagoRechazado = pagoService.buscarPorId(request.getIdPago());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pagoRechazado);
        }
    }
    
    @GetMapping("/reporte/ingresos")
    public ResponseEntity<byte[]> reporteIngresos() {
        byte[] pdf = pagoService.generarReporteIngresos();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-ingresos.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
