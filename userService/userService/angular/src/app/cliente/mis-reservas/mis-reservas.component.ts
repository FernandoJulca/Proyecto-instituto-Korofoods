import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ReservaServiceService } from '../service/reserva-service.service';
import { CodigoVerificacionService } from '../service/codigo-verificacion.service';
import { AuthService } from '../../auth/service/auth.service';
import { ReservaResponseDTO } from '../../shared/dto/ReservaResponseDTO';
import { EstadoReserva } from '../../shared/enums/estadoReserva.enum';
import { TipoReserva } from '../../shared/enums/tipoReserva.enum';
import { UserService } from '../service/user.service';
import { ResultadoResponse } from '../../shared/dto/ResultadoResponse';
import { EnviarCodigoRequest } from '../../shared/dto/EnviarCodigoRequest';

@Component({
  selector: 'app-mis-reservas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mis-reservas.component.html',
  styleUrls: ['./mis-reservas.component.css'],
})
export class MisReservasComponent implements OnInit {
  reservas: ReservaResponseDTO[] = [];
  reservasFiltradas: ReservaResponseDTO[] = [];
  reservaSeleccionada: ReservaResponseDTO | null = null;
  loading = false;
  error: string | null = null;

  // Filtros
  filtroFecha: string = '';
  filtroEstado: string = '';
  estadosDisponibles = Object.values(EstadoReserva);

  // Modales
  mostrarModalDetalle = false;
  mostrarModalEnviarCodigo = false;
  mostrarModalCancelar = false;
  mostrarMensaje = false;
  mensaje: string = '';
  tipoMensaje: 'success' | 'error' | 'info' = 'info';

  // Usuario
  idUsuario: number | null = null;

  // Enums para el template
  EstadoReserva = EstadoReserva;
  TipoReserva = TipoReserva;

  // ─────────────────────────────────────────────────────────────────
  // ZONA HORARIA: Perú es UTC-5 (sin cambio de horario de verano)
  // ─────────────────────────────────────────────────────────────────
  private readonly PERU_OFFSET_HOURS = -5;

  constructor(
    private reservaService: ReservaServiceService,
    private codigoService: CodigoVerificacionService,
    private authService: AuthService,
    private userService: UserService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.verificarSesion();
  }

  // ─────────────────────────────────────────────────────────────────
  // 🕐 ZONA HORARIA
  // ─────────────────────────────────────────────────────────────────

  /**
   * Parsea un ISO "YYYY-MM-DDTHH:mm:ss" del backend SIN dejar que
   * el navegador aplique su zona horaria local. Lo trata como si
   * el backend enviara la hora tal cual (hora del servidor).
   *
   * Esto evita el bug donde "2025-02-19T20:00:00" pasa a ser
   * "2025-02-20" al construir con `new Date(iso)` en UTC.
   */
  private parseFechaISO(iso: string | null | undefined): Date | null {
    if (!iso) return null;
    const [datePart, timePart = '00:00:00'] = iso.split('T');
    const [anio, mes, dia] = datePart.split('-').map(Number);
    const [hh, mm, ss] = timePart.split(':').map(Number);
    if (!anio || isNaN(anio)) return null;
    // Construye la Date en hora LOCAL del navegador (evita conversión UTC)
    return new Date(anio, mes - 1, dia, hh ?? 0, mm ?? 0, ss ?? 0);
  }

  /**
   * Retorna la hora actual ajustada a UTC-5 (Lima, Perú).
   */
  getNowPeru(): Date {
    const now = new Date();
    const utcMs = now.getTime() + now.getTimezoneOffset() * 60_000;
    return new Date(utcMs + this.PERU_OFFSET_HOURS * 3_600_000);
  }

  /**
   * Convierte una fecha parseada del backend a su equivalente en hora peruana.
   * Asume que el ISO del backend está en la zona horaria del servidor
   * y necesita ajustarse a UTC-5.
   *
   * Uso: para comparar fechas de reserva con la fecha elegida en el filtro.
   */
  private fechaEnPeru(iso: string | null | undefined): Date | null {
    const parsed = this.parseFechaISO(iso);
    if (!parsed) return null;

    // Convertir la fecha "local del servidor" a UTC real
    const utcMs = parsed.getTime() - parsed.getTimezoneOffset() * 60_000;
    // Luego aplicar offset de Perú (UTC-5)
    return new Date(utcMs + this.PERU_OFFSET_HOURS * 3_600_000);
  }

  /**
   * Extrae solo la parte "YYYY-MM-DD" de una fecha de reserva
   * ajustada a la zona horaria de Lima/Perú.
   *
   * Ejemplo: "2025-02-19T22:00:00" (servidor en UTC) → "2025-02-19" en Perú
   * Con new Date() nativo daría "2025-02-20" → BUG del filtro que se corrige aquí.
   */
  private getFechaLocalPeru(iso: string): string {
    const fechaPeru = this.fechaEnPeru(iso);
    if (!fechaPeru) return '';

    const anio = fechaPeru.getFullYear();
    const mes = String(fechaPeru.getMonth() + 1).padStart(2, '0');
    const dia = String(fechaPeru.getDate()).padStart(2, '0');
    return `${anio}-${mes}-${dia}`;
  }

  // ─────────────────────────────────────────────────────────────────
  // SESIÓN Y CARGA
  // ─────────────────────────────────────────────────────────────────

  verificarSesion(): void {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }

    this.userService.currentUser$.subscribe({
      next: (user) => {
        if (user && user.idUsuario) {
          this.idUsuario = user.idUsuario;
          this.cargarReservas();
        } else {
          this.authService.getUsuario().subscribe({
            next: (userData) => {
              this.idUsuario = userData.idUsuario;
              this.userService.setUser(userData);
              this.cargarReservas();
            },
            error: (err) => {
              console.error('Error al obtener usuario:', err);
              this.router.navigate(['/login']);
            },
          });
        }
      },
      error: (err) => {
        console.error('Error en suscripción de usuario:', err);
        this.router.navigate(['/login']);
      },
    });
  }

  cargarReservas(): void {
    if (!this.idUsuario) return;

    this.loading = true;
    this.error = null;

    this.reservaService.listarMisReservas(this.idUsuario).subscribe({
      next: (response: ResultadoResponse<ReservaResponseDTO[]>) => {
        this.reservas = response.data;
        this.aplicarFiltros();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al cargar reservas:', err);
        this.error = 'Error al cargar las reservas';
        this.loading = false;
      },
    });
  }

  // ─────────────────────────────────────────────────────────────────
  // FILTROS — con comparación de fecha en hora peruana
  // ─────────────────────────────────────────────────────────────────

  aplicarFiltros(): void {
    this.reservasFiltradas = this.reservas.filter((reserva) => {
      let cumpleFecha = true;
      let cumpleEstado = true;

      if (this.filtroFecha) {
        // ✅ Comparamos la fecha de la reserva convertida a hora peruana
        // contra la fecha elegida en el filtro (que viene como "YYYY-MM-DD").
        // Antes se usaba new Date(reserva.fechaHora).toISOString().split('T')[0]
        // lo que podía dar un día distinto al del servidor por la conversión UTC.
        const fechaReservaEnPeru = this.getFechaLocalPeru(reserva.fechaHora);
        cumpleFecha = fechaReservaEnPeru === this.filtroFecha;
      }

      if (this.filtroEstado) {
        cumpleEstado = reserva.estado === this.filtroEstado;
      }

      return cumpleFecha && cumpleEstado;
    });
  }

  onFiltroFechaChange(): void {
    this.aplicarFiltros();
  }

  onFiltroEstadoChange(): void {
    this.aplicarFiltros();
  }

  limpiarFiltros(): void {
    this.filtroFecha = '';
    this.filtroEstado = '';
    this.aplicarFiltros();
  }

  // ─────────────────────────────────────────────────────────────────
  // MODALES Y ACCIONES
  // ─────────────────────────────────────────────────────────────────

  verDetalle(reserva: ReservaResponseDTO): void {
    this.reservaSeleccionada = reserva;
    this.mostrarModalDetalle = true;
  }

  cerrarModalDetalle(): void {
    this.mostrarModalDetalle = false;
    this.reservaSeleccionada = null;
  }

  puedeEnviarCodigo(estado: EstadoReserva): boolean {
    return estado === EstadoReserva.PAGADA;
  }

  puedeCancelarReserva(estado: EstadoReserva): boolean {
    return (
      estado === EstadoReserva.PENDIENTE || estado === EstadoReserva.PAGADA
    );
  }

  abrirModalEnviarCodigo(reserva: ReservaResponseDTO): void {
    this.reservaSeleccionada = reserva;
    this.mostrarModalEnviarCodigo = true;
  }

  enviarCodigoVerificacion(tipoEnvio: 'SMS' | 'EMAIL'): void {
    if (!this.reservaSeleccionada) return;

    const request: EnviarCodigoRequest = {
      reservaId: this.reservaSeleccionada.idReserva,
      tipoEnvio: tipoEnvio,
    };

    this.codigoService.enviarCodigo(request).subscribe({
      next: () => {
        this.mostrarModalEnviarCodigo = false;
        this.mostrarMensajeExito(
          `Código de verificación enviado por ${tipoEnvio} exitosamente`,
        );
      },
      error: (err) => {
        console.error('Error al enviar código:', err);
        this.mostrarMensajeError('Error al enviar el código de verificación');
      },
    });
  }

  abrirModalCancelar(reserva: ReservaResponseDTO): void {
    this.reservaSeleccionada = reserva;
    this.mostrarModalCancelar = true;
  }

  confirmarCancelacion(): void {
    if (!this.reservaSeleccionada) return;

    this.reservaService
      .cancelarReserva(this.reservaSeleccionada.idReserva)
      .subscribe({
        next: () => {
          this.mostrarModalCancelar = false;
          this.mostrarMensajeExito('Reserva cancelada exitosamente');
          this.cargarReservas();
        },
        error: (err) => {
          console.error('Error al cancelar reserva:', err);
          this.mostrarMensajeError('Error al cancelar la reserva');
        },
      });
  }

  cerrarModalEnviarCodigo(): void {
    this.mostrarModalEnviarCodigo = false;
    this.reservaSeleccionada = null;
  }

  cerrarModalCancelar(): void {
    this.mostrarModalCancelar = false;
    this.reservaSeleccionada = null;
  }

  // ─────────────────────────────────────────────────────────────────
  // MENSAJES TOAST
  // ─────────────────────────────────────────────────────────────────

  mostrarMensajeExito(mensaje: string): void {
    this.mensaje = mensaje;
    this.tipoMensaje = 'success';
    this.mostrarMensaje = true;
    setTimeout(() => (this.mostrarMensaje = false), 4000);
  }

  mostrarMensajeError(mensaje: string): void {
    this.mensaje = mensaje;
    this.tipoMensaje = 'error';
    this.mostrarMensaje = true;
    setTimeout(() => (this.mostrarMensaje = false), 4000);
  }

  cerrarMensaje(): void {
    this.mostrarMensaje = false;
  }

  // ─────────────────────────────────────────────────────────────────
  // FORMATEO DE FECHAS — usando parseFechaISO (sin bug de UTC)
  // ─────────────────────────────────────────────────────────────────

  formatearFecha(fecha: string): string {
    const date = this.parseFechaISO(fecha);
    if (!date) return '—';
    const opciones: Intl.DateTimeFormatOptions = {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    };
    return date.toLocaleDateString('es-ES', opciones);
  }

  formatearFechaCorta(fecha: string): string {
    const date = this.parseFechaISO(fecha);
    if (!date) return '—';
    return date.toLocaleDateString('es-ES', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
    });
  }

  formatearHora(fecha: string): string {
    const date = this.parseFechaISO(fecha);
    if (!date) return '—';
    return date.toLocaleTimeString('es-ES', {
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  // ─────────────────────────────────────────────────────────────────
  // CLASES CSS
  // ─────────────────────────────────────────────────────────────────

  getEstadoClass(estado: EstadoReserva): string {
    const clases: { [key in EstadoReserva]: string } = {
      [EstadoReserva.PENDIENTE]: 'estado-pendiente',
      [EstadoReserva.PAGADA]: 'estado-pagada',
      [EstadoReserva.ASISTIDA]: 'estado-asistida',
      [EstadoReserva.CANCELADA]: 'estado-cancelada',
      [EstadoReserva.VENCIDA]: 'estado-vencida',
    };
    return clases[estado];
  }

  getTipoReservaClass(tipo: TipoReserva): string {
    return tipo === TipoReserva.ESPECIAL ? 'tipo-especial' : 'tipo-simple';
  }

  getNombreCompleto(reserva: ReservaResponseDTO): string {
    return `${reserva.nombreCli} ${reserva.apellidoPa} ${reserva.apellidoMa}`;
  }
}
