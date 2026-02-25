import { Component, inject, OnInit, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReservaServiceService } from '../../cliente/service/reserva-service.service';
import { CodigoVerificacionService } from '../../cliente/service/codigo-verificacion.service';
import { ReservaAsistidaDTO } from '../../shared/dto/ReservaAsistidaDTO';
import { VerificarCodigoRequest } from '../../shared/dto/VerificarCodigoRequest';

type FiltroTipo = 'AMBAS' | 'SIMPLE' | 'ESPECIAL';

interface AlertaVerificacion {
  clase: string;
  icono: string;
  mensaje: string;
}

@Component({
  selector: 'app-listado-reservas-asistidas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './listado-reservas-asistidas.component.html',
  styleUrls: ['./listado-reservas-asistidas.component.css'],
  encapsulation: ViewEncapsulation.None // ← Esto desactiva la encapsulación
})
export class ListadoReservasAsistidasComponent implements OnInit {
  private reservaService = inject(ReservaServiceService);
  private codigoService = inject(CodigoVerificacionService);

  reservas: ReservaAsistidaDTO[] = [];
  reservasFiltradas: ReservaAsistidaDTO[] = [];

  loading = true;
  error: string | null = null;

  filtroTipo: FiltroTipo = 'AMBAS';
  textoBusqueda: string = '';

  fechaFormateada = '';

  modalAbierto = false;
  codigoIngresado = '';
  idReservaInput: number | null = null;
  verificando = false;
  alertaVerificacion: AlertaVerificacion | null = null;

  ngOnInit(): void {
    this.formatearFechaHeader();
    this.cargarReservas();
  }

  private formatearFechaHeader(): void {
    const raw = new Date().toLocaleDateString('es-ES', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
    this.fechaFormateada = raw.charAt(0).toUpperCase() + raw.slice(1);
  }

  cargarReservas(): void {
    this.loading = true;
    this.error = null;

    this.reservaService.listarReservasAsistidasHoy().subscribe({
      next: (resp) => {
        this.reservas = resp.data ?? [];
        this.aplicarFiltros();
        this.loading = false;
      },
      error: () => {
        this.error = 'Error al cargar las reservas. Intenta nuevamente.';
        this.loading = false;
      },
    });
  }

  cambiarFiltroTipo(tipo: FiltroTipo): void {
    this.filtroTipo = tipo;
    this.aplicarFiltros();
  }

  aplicarFiltros(): void {
    let resultado = [...this.reservas];

    // Filtro tipo — usa tipoReserva directo del DTO
    if (this.filtroTipo === 'SIMPLE') {
      resultado = resultado.filter((r) => r.tipoReserva === 'SIMPLE');
    } else if (this.filtroTipo === 'ESPECIAL') {
      resultado = resultado.filter((r) => r.tipoReserva === 'ESPECIAL');
    }

    // Búsqueda libre
    const term = this.textoBusqueda.toLowerCase().trim();
    if (term) {
      resultado = resultado.filter((r) =>
        [
          r.nombreCliente,
          r.zona,
          r.evento,
          r.tematica,
          r.observaciones,
          r.tipoReserva,
          String(r.mesa),
          String(r.idReserva),
        ].some((v) => v?.toLowerCase().includes(term)),
      );
    }

    this.reservasFiltradas = resultado;
  }

  limpiarBusqueda(): void {
    this.textoBusqueda = '';
    this.aplicarFiltros();
  }

  abrirModalVerificar(): void {
    this.modalAbierto = true;
    this.codigoIngresado = '';
    this.idReservaInput = null;
    this.alertaVerificacion = null;
    this.verificando = false;
  }

  cerrarModal(): void {
    if (this.verificando) return;
    this.modalAbierto = false;
  }

  confirmarVerificacion(): void {
    if (!this.codigoIngresado || !this.idReservaInput || this.verificando)
      return;

    this.verificando = true;
    this.alertaVerificacion = null;

    const request: VerificarCodigoRequest = {
      reservaId: this.idReservaInput,
      codigo: this.codigoIngresado.trim(),
    };

    this.codigoService.verificarCodigo(request).subscribe({
      next: (resp) => {
        this.verificando = false;
        if (resp.valor) {
          this.alertaVerificacion = {
            clase: 'alerta-success',
            icono: 'fa-solid fa-circle-check',
            mensaje: resp.mensaje!,
          };
          setTimeout(() => {
            this.cerrarModal();
            this.cargarReservas();
          }, 1800);
        } else {
          this.alertaVerificacion = this.resolverAlertaError(resp.mensaje!);
        }
      },
      error: () => {
        this.verificando = false;
        this.alertaVerificacion = {
          clase: 'alerta-danger',
          icono: 'fa-solid fa-triangle-exclamation',
          mensaje: 'Error de conexión. Intenta nuevamente.',
        };
      },
    });
  }

  private resolverAlertaError(mensaje: string): AlertaVerificacion {
    const m = mensaje.toLowerCase();

    if (m.includes('expirado')) {
      return { clase: 'alerta-warning', icono: 'fa-solid fa-clock', mensaje };
    }
    if (m.includes('ya fue verificada')) {
      return {
        clase: 'alerta-info',
        icono: 'fa-solid fa-circle-info',
        mensaje,
      };
    }
    if (m.includes('cancelada')) {
      return { clase: 'alerta-danger', icono: 'fa-solid fa-ban', mensaje };
    }
    return {
      clase: 'alerta-danger',
      icono: 'fa-solid fa-triangle-exclamation',
      mensaje,
    };
  }

  getIniciales(nombre: string): string {
    if (!nombre) return '?';
    return nombre
      .split(' ')
      .slice(0, 2)
      .map((p) => p.charAt(0).toUpperCase())
      .join('');
  }

  formatearFecha(iso: string | null): string {
    if (!iso) return '—';
    return new Date(iso).toLocaleDateString('es-ES', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
    });
  }

  formatearHora(iso: string | null): string {
    if (!iso) return '—';
    return new Date(iso).toLocaleTimeString('es-ES', {
      hour: '2-digit',
      minute: '2-digit',
    });
  }
}
