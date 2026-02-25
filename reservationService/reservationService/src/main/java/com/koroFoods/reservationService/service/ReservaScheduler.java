package com.koroFoods.reservationService.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.koroFoods.reservationService.enums.EstadoReserva;
import com.koroFoods.reservationService.model.Reserva;
import com.koroFoods.reservationService.repository.IReservaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservaScheduler {

    private final IReservaRepository reservaRepository;
    private final ReservaService reservaService;

    @Scheduled(cron = "0 0/30 * * * *")
    public void verificarReservasVencidas() {
        LocalDateTime ahora = LocalDateTime.now();

        // Una reserva vence cuando: fechaHora + 2h < ahora
        LocalDateTime limiteTolerancia = ahora.minusHours(2);

        log.info("🕐 [{}] Verificando reservas vencidas (tolerancia hasta {})",
            ahora, limiteTolerancia);

        List<Reserva> vencidas = reservaRepository.findReservasVencidas(limiteTolerancia);

        if (vencidas.isEmpty()) {
            log.info("✅ No hay reservas vencidas");
            return;
        }

        vencidas.forEach(reserva -> {
            reserva.setEstado(EstadoReserva.VENCIDA);
            reservaRepository.save(reserva);
            log.info("🔴 Reserva #{} vencida | Slot: {} | Límite era: {}",
                reserva.getIdReserva(),
                reserva.getFechaHora(),
                reserva.getFechaHora().plusHours(2));
        });

        log.info("⚠️ {} reservas marcadas como VENCIDAS", vencidas.size());
    }
}