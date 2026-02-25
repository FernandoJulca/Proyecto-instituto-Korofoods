package com.koroFoods.reservationService.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.koroFoods.reservationService.config.VerificacionConfig;
import com.koroFoods.reservationService.dto.CodigoVerificacionResponse;
import com.koroFoods.reservationService.dto.EnviarCodigoRequest;
import com.koroFoods.reservationService.dto.ResultadoResponse;
import com.koroFoods.reservationService.dto.VerificarCodigoRequest;
import com.koroFoods.reservationService.enums.EstadoReserva;
import com.koroFoods.reservationService.feign.MesaFeign;
import com.koroFoods.reservationService.feign.MesaFeignClient;
import com.koroFoods.reservationService.feign.UsuarioFeign;
import com.koroFoods.reservationService.feign.UsuarioFeignClient;
import com.koroFoods.reservationService.model.Reserva;
import com.koroFoods.reservationService.repository.IReservaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificacionService {
    
    private final IReservaRepository reservaRepository;
    private final UsuarioFeignClient usuarioFeignClient;
    private final MesaFeignClient mesaFeignClient;
    private final EmailService emailService;
    private final SmsService smsService;
    private final VerificacionConfig verificacionConfig;
    
    /**
     * Genera y envía código de verificación
     */
    @Transactional
    public CodigoVerificacionResponse enviarCodigoVerificacion(EnviarCodigoRequest request) {
    	
        Reserva reserva = reservaRepository.findById(request.getReservaId())
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        
        if (reserva.getEstado() == EstadoReserva.ASISTIDA) {
            throw new RuntimeException("La reserva ya fue verificada");
        }
        
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new RuntimeException("No se puede verificar una reserva cancelada");
        }
        
        ResultadoResponse<UsuarioFeign> usuarioResponse = usuarioFeignClient
            .getUsuarioById(reserva.getIdUsuario());
        
        if (usuarioResponse == null || usuarioResponse.getData() == null) {
            throw new RuntimeException("Usuario no encontrado en el microservicio");
        }
        
        UsuarioFeign usuario = usuarioResponse.getData();
        
        String codigo = generarCodigoVerificacion();
        LocalDateTime fechaExpiracion = LocalDateTime.now()
            .plusMinutes(verificacionConfig.getExpiracionMinutos());
        
        reserva.setCodigoVerificacion(codigo);
        reserva.setFechaExpCod(fechaExpiracion);
        reserva.setVerificado(false);
        reservaRepository.save(reserva);
        
        if ("SMS".equalsIgnoreCase(request.getTipoEnvio())) {
            if (usuario.getTelefono() == null || usuario.getTelefono().isBlank()) {
                throw new RuntimeException("El usuario no tiene número de teléfono registrado");
            }
            smsService.enviarCodigoVerificacion(
                usuario.getTelefono(), 
                codigo, 
                usuario.getNombreCompleto()
            );
        } else if ("EMAIL".equalsIgnoreCase(request.getTipoEnvio())) {
            if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {
                throw new RuntimeException("El usuario no tiene correo registrado");
            }
            emailService.enviarCodigoVerificacion(
                usuario.getCorreo(), 
                codigo, 
                usuario.getNombreCompleto()
            );
        } else {
            throw new RuntimeException("Tipo de envío no válido");
        }
        
        log.info("Código de verificación enviado para reserva {} por {}", 
            reserva.getIdReserva(), request.getTipoEnvio());
        
        return new CodigoVerificacionResponse(
            "Código enviado exitosamente",
            fechaExpiracion,
            request.getTipoEnvio()
        );
    }
    
    /**
     * Verifica el código y cambia estado a ASISTIDA
     */
    @Transactional
    public ResultadoResponse<String> verificarCodigo(VerificarCodigoRequest request) {

        Reserva reserva = reservaRepository.findById(request.getReservaId())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (reserva.getEstado() == EstadoReserva.ASISTIDA) {
            return ResultadoResponse.error("La reserva ya fue verificada anteriormente");
        }
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            return ResultadoResponse.error("No se puede verificar una reserva cancelada");
        }
        if (reserva.getCodigoVerificacion() == null) {
            return ResultadoResponse.error("No se ha generado un código de verificación");
        }
        if (LocalDateTime.now().isAfter(reserva.getFechaExpCod())) {
            return ResultadoResponse.error("El código de verificación ha expirado");
        }
        if (!reserva.getCodigoVerificacion().equals(request.getCodigo())) {
            return ResultadoResponse.error("Código de verificación incorrecto");
        }

        reserva.setEstado(EstadoReserva.ASISTIDA);
        reserva.setVerificado(true);
        reservaRepository.save(reserva);

        if (reserva.getIdMesa() != null) {
            try {
                mesaFeignClient.cambiarEstadoMesaOcupada(reserva.getIdMesa());
                log.info("Mesa {} marcada como OCUPADA", reserva.getIdMesa());
            } catch (Exception e) {
                log.warn("No se pudo actualizar estado de mesa {}: {}", reserva.getIdMesa(), e.getMessage());
            }
        }

        log.info("Reserva {} verificada exitosamente y marcada como ASISTIDA", reserva.getIdReserva());

        return ResultadoResponse.success(
            "Código verificado correctamente. Reserva confirmada como ASISTIDA",
            "ASISTIDA"
        );
    }
    
    /**
     * Genera código de verificación numérico
     */
    private String generarCodigoVerificacion() {
        Random random = new Random();
        StringBuilder codigo = new StringBuilder();
        
        for (int i = 0; i < verificacionConfig.getLongitud(); i++) {
            codigo.append(random.nextInt(10));
        }
        
        return codigo.toString();
    }
}