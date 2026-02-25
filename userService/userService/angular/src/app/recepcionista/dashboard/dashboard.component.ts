import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { EventoClienteService } from '../../cliente/service/eventoClienteService';
import { ReservaServiceService } from '../../cliente/service/reserva-service.service';
import { EventoFeignReserva } from '../../shared/dto/EventoFeignReserva';
import { RecepcionistaCountsDTO } from '../../shared/dto/RecepcionistaCountsDTO';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  private eventoService = inject(EventoClienteService);
  private reservaService = inject(ReservaServiceService);

  // Data
  counts: RecepcionistaCountsDTO = {
    reservasHoy: 0,
    reservasAsistidas: 0,
    reservasPendientes: 0,
    reservasTomorrow: 0,
  };

  reservasHoyFijo: number = 0;
  primeraCarga: boolean = true;

  eventosDelDia: EventoFeignReserva[] = [];

  // Estados
  loading = true;
  error: string | null = null;

  // Fecha actual
  fechaFormateada = '';

  ngOnInit(): void {
    this.formatearFecha();
    this.cargarDatosDashboard();
  }

  private formatearFecha(): void {
    const opciones: Intl.DateTimeFormatOptions = {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    };
    const raw = new Date().toLocaleDateString('es-ES', opciones);
    this.fechaFormateada = raw.charAt(0).toUpperCase() + raw.slice(1);
  }

  cargarDatosDashboard(): void {
    this.loading = true;
    this.error = null;

    forkJoin({
      counts: this.reservaService.obtenerCountsRecepcionista(),
      eventos: this.eventoService.listarEventosDelDia(),
    }).subscribe({
      next: ({ counts, eventos }) => {
        if (this.primeraCarga) {
          this.reservasHoyFijo = counts.reservasHoy;
          this.counts.reservasTomorrow = counts.reservasTomorrow;
          this.primeraCarga = false;
        }

        this.counts.reservasHoy = this.reservasHoyFijo;
        this.counts.reservasAsistidas = counts.reservasAsistidas;
        this.counts.reservasPendientes = this.reservasHoyFijo - counts.reservasAsistidas,
        this.eventosDelDia = (eventos as any)?.data ?? [];
        this.loading = false;
      },
      error: (err) => {
        console.error('Error cargando datos del dashboard:', err);
        this.error =
          'Error al cargar los datos. Por favor, intenta nuevamente.';
        this.loading = false;
      },
    });
  }

  // ─── ESTADO TEMPORAL ──────────────────────────────────────────────────────
  getEstadoEvento(evento: EventoFeignReserva): {
    label: string;
    clase: string;
    icono: string;
  } {
    const ahora = new Date();
    const inicio = new Date(evento.fechaInicio);
    const fin = new Date(evento.fechaFin);

    if (ahora < inicio) {
      const mins = Math.round((inicio.getTime() - ahora.getTime()) / 60000);
      const label =
        mins < 60
          ? `Comienza en ${mins} min`
          : `Comienza a las ${this.formatearHora(evento.fechaInicio)}`;
      return {
        label,
        clase: 'estado-proximo',
        icono: 'fa-solid fa-hourglass-start',
      };
    }

    if (ahora >= inicio && ahora <= fin) {
      return { label: 'En curso', clase: 'estado-en-curso', icono: '' };
    }

    return {
      label: 'Finalizado',
      clase: 'estado-finalizado',
      icono: 'fa-solid fa-check',
    };
  }

  // ─── HELPERS ──────────────────────────────────────────────────────────────
  getEmojiTematica(tematica: string | undefined): string {
    if (!tematica) return '🎭';
    const t = tematica.toLowerCase();
    if (t.includes('anime')) return '⛩️';
    if (t.includes('pelicul') || t.includes('cine')) return '🎬';
    if (t.includes('comic') || t.includes('marvel') || t.includes('héroe'))
      return '🦸';
    if (t.includes('infantil') || t.includes('pokemon')) return '🎈';
    if (t.includes('videojuego') || t.includes('gamer')) return '🎮';
    if (t.includes('serie')) return '📺';
    if (t.includes('cultural')) return '🎨';
    if (t.includes('manwha') || t.includes('bl')) return '📖';
    return '🎭';
  }

  formatearHora(fechaISO: string): string {
    return new Date(fechaISO).toLocaleTimeString('es-ES', {
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  trackByEventoId(_index: number, evento: EventoFeignReserva): number {
    return evento.idEvento;
  }
}
