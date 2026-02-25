import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { EventoFeignReserva } from '../../shared/dto/EventoFeignReserva';
import { EventoConMesaDto } from '../../shared/dto/EventoConMesaDto';
import { EventoDto } from '../../shared/dto/EventoDto';
import { EventoClienteService } from '../service/eventoClienteService';
import { ReservaServiceService } from '../service/reserva-service.service';
import { ReservaRequest } from '../../shared/request/ReservaRequest';
import { AuthService } from '../../auth/service/auth.service';
import { PagoService } from '../service/pago.service';
import { CrearPagoRequest } from '../pago/pagoDto';

@Component({
  selector: 'app-eventos-tematicos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './eventos-tematicos.component.html',
  styleUrl: './eventos-tematicos.component.css',
})
export class EventosTematicosComponent implements OnInit {
  paso = 1;

  eventos: EventoFeignReserva[] = [];
  eventosFiltrados: EventoFeignReserva[] = [];
  cargando = false;

  filtroNombre = '';
  filtroFechaDesde = '';
  filtroFechaHasta = '';
  filtroAforo: number | null = null;

  eventoDetalle: EventoFeignReserva | null = null;

  cantidadPersonas = 1;
  mostrarAlertaAforo = false;
  mostrarAlertaMesa = false;
  cargandoMesas = false;

  mesasDisponibles: EventoConMesaDto[] = [];
  mesaSeleccionada: EventoConMesaDto | null = null;
  mesaOcupadaMap: Record<number, boolean> = {};

  metodoPago: 'TARJETA' | 'YAPE' | 'PLIN' = 'TARJETA';
  imagenPreview: string | null = null;
  procesandoPago = false;
  loading = false;
  errorPago: string | null = null;

  qrYapeUrl = '/assets/yape-qr.jpeg';
  qrPlinUrl = '/assets/plin-qr.jpeg';

  datosTarjeta = { numero: '', nombre: '', fechaExpiracion: '', cvv: '' };

  mostrarAlerta = false;
  alertaTipo: 'exito' | 'error' = 'exito';
  alertaDatos: { mensaje?: string } = {};

  // Propiedades para autenticación
  usuarioAutenticado = false;
  idUsuario: number | null = null;

  // ─────────────────────────────────────────────────────────────────
  // ZONA HORARIA: Perú es UTC-5 (sin cambio de horario de verano)
  // ─────────────────────────────────────────────────────────────────
  private readonly PERU_OFFSET_HOURS = -5;

  constructor(
    private eventoService: EventoClienteService,
    private reservaService: ReservaServiceService,
    private authService: AuthService,
    private router: Router,
    private pagoService: PagoService,
  ) {}

  ngOnInit(): void {
    this.imprimirInfoZonaHoraria();
    this.cargarEventos();
    this.verificarSesion();
  }

  // ─────────────────────────────────────────────────────────────────
  // 🕐 ZONA HORARIA — diagnóstico y hora actual en Perú
  // ─────────────────────────────────────────────────────────────────

  /**
   * Retorna la fecha/hora actual ajustada a UTC-5 (Lima, Perú).
   * Funciona aunque el servidor/navegador esté en cualquier zona horaria.
   */
  getNowPeru(): Date {
    const nowUtc = new Date(); // hora real en UTC
    const utcMs = nowUtc.getTime() + nowUtc.getTimezoneOffset() * 60_000;
    return new Date(utcMs + this.PERU_OFFSET_HOURS * 3_600_000);
  }

  /**
   * Imprime en consola la hora del servidor (local) y la hora equivalente
   * en la zona horaria de Lima/Perú para diagnóstico.
   */
  private imprimirInfoZonaHoraria(): void {
    const ahora = new Date();
    const offsetMin = ahora.getTimezoneOffset(); // minutos detrás de UTC
    const offsetHoras = -(offsetMin / 60); // p.ej. -5, +8, etc.
    const zonaNombre = Intl.DateTimeFormat().resolvedOptions().timeZone;

    const ahoraPeru = this.getNowPeru();

    console.group('🌐 Diagnóstico de Zona Horaria');
    console.log(
      `🖥️  Hora del SERVIDOR/NAVEGADOR : ${ahora.toLocaleString()} (${zonaNombre}, UTC${offsetHoras >= 0 ? '+' : ''}${offsetHoras})`,
    );
    console.log(
      `🇵🇪  Hora en LIMA / PERÚ (UTC-5): ${ahoraPeru.toLocaleString('es-PE')}`,
    );
    console.log(
      `⏱️  Diferencia respecto a Perú  : ${offsetHoras - this.PERU_OFFSET_HOURS >= 0 ? '+' : ''}${offsetHoras - this.PERU_OFFSET_HOURS} horas`,
    );
    console.groupEnd();
  }
  
  eventoVencido(evento: EventoFeignReserva): boolean {
    const isoFin = evento.fechaFin ?? evento.fechaInicio;
    if (!isoFin) return false;

    // Parsea el ISO del backend como si fuera hora peruana (UTC-5)
    // añadiendo el offset explícitamente para que getTime() sea correcto en UTC real
    const fechaFinPeru = new Date(isoFin + '-05:00'); // fuerza interpretación UTC-5

    const ahoraUtc = new Date();

    return fechaFinPeru <= ahoraUtc; // compara en UTC real, sin magia
  }

  // ─────────────────────────────────────────────────────────────────
  // AUTENTICACIÓN
  // ─────────────────────────────────────────────────────────────────

  verificarSesion(): void {
    this.usuarioAutenticado = this.authService.isLoggedIn();

    if (this.usuarioAutenticado) {
      this.authService.getUsuario().subscribe({
        next: (response) => {
          this.idUsuario = response.idUsuario;
          console.log('✅ ID Usuario obtenido:', this.idUsuario);
        },
        error: (error) => {
          console.error('❌ Error al obtener usuario:', error);
          this.usuarioAutenticado = false;
          this.idUsuario = null;
        },
      });
    } else {
      this.idUsuario = null;
    }
  }

  // ─────────────────────────────────────────────────────────────────
  // CARGA Y FILTRADO DE EVENTOS
  // ─────────────────────────────────────────────────────────────────

  cargarEventos(): void {
    this.cargando = true;
    this.eventos = [];
    this.eventoService.listarEventos().subscribe({
      next: (res) => {
        if (!res.valor || !res.data?.length) {
          console.log(res);
          this.cargando = false;
          return;
        }
        const ids: number[] = res.data.map((e: EventoDto) => e.idEvento);
        let count = 0;
        ids.forEach((id) => {
          this.eventoService.obtenerEventoValidado(id).subscribe({
            next: (detRes) => {
              console.log('Evento', detRes);
              if (detRes.valor && detRes.data) this.eventos.push(detRes.data);
            },
            complete: () => {
              count++;
              if (count === ids.length) this.finalizarCarga();
            },
            error: () => {
              count++;
              if (count === ids.length) this.finalizarCarga();
            },
          });
        });
      },
      error: () => {
        this.cargando = false;
      },
    });
  }

  private finalizarCarga(): void {
    // Ordenar por fecha de inicio
    this.eventos.sort(
      (a, b) =>
        (this.parseFecha(a.fechaInicio)?.getTime() ?? 0) -
        (this.parseFecha(b.fechaInicio)?.getTime() ?? 0),
    );

    // ✅ Descartar eventos cuya fechaFin ya pasó en hora peruana
    const vencidos = this.eventos.filter((e) => this.eventoVencido(e));
    if (vencidos.length) {
      console.info(
        `🇵🇪 Se filtraron ${vencidos.length} evento(s) vencido(s) según hora de Lima/Perú:`,
        vencidos.map(
          (e) => `${e.nombre} (fin: ${e.fechaFin ?? e.fechaInicio})`,
        ),
      );
    }

    this.eventos = this.eventos.filter((e) => !this.eventoVencido(e));
    this.eventosFiltrados = [...this.eventos];
    this.cargando = false;
  }

  aplicarFiltros(): void {
    let r = [...this.eventos];
    if (this.filtroNombre.trim()) {
      const q = this.filtroNombre.toLowerCase();
      r = r.filter(
        (e) =>
          e.nombre.toLowerCase().includes(q) ||
          e.descripcion.toLowerCase().includes(q) ||
          (e.tematica?.toLowerCase().includes(q) ?? false),
      );
    }
    if (this.filtroFechaDesde) {
      const desde = new Date(this.filtroFechaDesde);
      r = r.filter(
        (e) =>
          (this.parseFecha(e.fechaInicio)?.getTime() ?? 0) >= desde.getTime(),
      );
    }
    if (this.filtroFechaHasta) {
      const hasta = new Date(this.filtroFechaHasta);
      hasta.setHours(23, 59, 59);
      r = r.filter(
        (e) =>
          (this.parseFecha(e.fechaInicio)?.getTime() ?? 0) <= hasta.getTime(),
      );
    }
    if (this.filtroAforo !== null && this.filtroAforo > 0)
      r = r.filter((e) => (e.aforo ?? 0) >= this.filtroAforo!);

    // ✅ Reaplica el filtro de vencidos por si se usa después de la carga
    this.eventosFiltrados = r.filter((e) => !this.eventoVencido(e));
  }

  seleccionarMetodoPago(metodo: 'TARJETA' | 'YAPE' | 'PLIN'): void {
    this.metodoPago = metodo;
    this.imagenPreview = null;
    this.errorPago = null;
  }

  limpiarFiltros(): void {
    this.filtroNombre = '';
    this.filtroFechaDesde = '';
    this.filtroFechaHasta = '';
    this.filtroAforo = null;
    this.eventosFiltrados = [...this.eventos];
  }

  seleccionarEvento(evento: EventoFeignReserva): void {
    this.eventoDetalle = evento;
    this.cantidadPersonas = 1;
    this.mostrarAlertaAforo = false;
    this.mostrarAlertaMesa = false;
    this.paso = 2;
  }

  volverListado(): void {
    this.paso = 1;
    this.eventoDetalle = null;
    this.cantidadPersonas = 1;
    this.mostrarAlertaAforo = false;
    this.mostrarAlertaMesa = false;
  }
  volverPersonas(): void {
    this.paso = 2;
    this.mesaSeleccionada = null;
    this.mesasDisponibles = [];
  }
  volverMesas(): void {
    this.paso = 3;
  }
  volverResumen(): void {
    this.paso = 4;
  }

  irAPago(): void {
    if (!this.usuarioAutenticado || !this.idUsuario) {
      alert('Debe iniciar sesión para completar la reserva');
      const reservaTemp = {
        evento: this.eventoDetalle,
        personas: this.cantidadPersonas,
        mesa: this.mesaSeleccionada,
      };
      localStorage.setItem(
        'reserva_evento_temporal',
        JSON.stringify(reservaTemp),
      );
      this.router.navigate(['/login']);
      return;
    }

    this.paso = 5;
    this.metodoPago = 'TARJETA';
    this.imagenPreview = null;
    this.errorPago = null;
  }

  validarPersonas(): void {
    if (!this.eventoDetalle) return;
    const aforo = this.eventoDetalle.aforo ?? 0;
    this.mostrarAlertaAforo = this.cantidadPersonas > aforo;
    this.mostrarAlertaMesa =
      this.cantidadPersonas > 1 && !this.mostrarAlertaAforo;
  }

  incrementarPersonas(): void {
    this.cantidadPersonas++;
    this.validarPersonas();
  }

  decrementarPersonas(): void {
    if (this.cantidadPersonas > 1) {
      this.cantidadPersonas--;
      this.validarPersonas();
    }
  }

  buscarMesas(): void {
    if (!this.eventoDetalle || this.mostrarAlertaAforo) return;
    this.cargandoMesas = true;
    this.mesasDisponibles = [];
    this.mesaSeleccionada = null;
    this.mesaOcupadaMap = {};
    this.eventoService
      .listarMesasPorEvento(this.eventoDetalle.idEvento, this.cantidadPersonas)
      .subscribe({
        next: (res) => {
          this.mesasDisponibles = res.valor && res.data ? res.data : [];
          this.cargandoMesas = false;
          this.paso = 3;
          this.validarOcupaciones(this.mesasDisponibles);
        },
        error: () => {
          this.mesasDisponibles = [];
          this.cargandoMesas = false;
          this.paso = 3;
        },
      });
  }

  private validarOcupaciones(mesas: EventoConMesaDto[]): void {
    if (!this.eventoDetalle || !mesas.length) return;
    const desde = this.parseFecha(this.eventoDetalle.fechaInicio)!;
    const hasta = this.parseFecha(this.eventoDetalle.fechaFin)!;
    mesas.forEach((mesa) => {
      this.eventoService
        .validarOcupacion(
          mesa.idMesa,
          this.eventoDetalle!.idEvento,
          desde,
          hasta,
        )
        .subscribe({
          next: (res) => {
            this.mesaOcupadaMap[mesa.idEventoMesa] = res.data === true;
          },
          error: () => {
            this.mesaOcupadaMap[mesa.idEventoMesa] = true;
          },
        });
    });
  }

  seleccionarMesa(mesa: EventoConMesaDto): void {
    if (this.mesaOcupadaMap[mesa.idEventoMesa]) return;
    this.mesaSeleccionada = mesa;
  }

  continuarAResumen(): void {
    if (this.mesaSeleccionada) this.paso = 4;
  }

  puedeConfirmarPago(): boolean {
    if (!this.usuarioAutenticado || !this.idUsuario) return false;
    if (this.metodoPago === 'TARJETA')
      return !!(
        this.datosTarjeta.numero &&
        this.datosTarjeta.nombre &&
        this.datosTarjeta.fechaExpiracion &&
        this.datosTarjeta.cvv
      );
    return !!this.imagenPreview;
  }

  confirmarPago(): void {
    if (!this.puedeConfirmarPago() || this.procesandoPago) return;

    if (!this.usuarioAutenticado || !this.idUsuario) {
      alert('Debe iniciar sesión para completar la reserva');
      return;
    }

    if (!this.mesaSeleccionada || !this.eventoDetalle) {
      console.error('Faltan datos para crear la reserva');
      return;
    }

    this.procesandoPago = true;
    this.errorPago = null;

    const reservaRequest: ReservaRequest = {
      idUsuario: this.idUsuario,
      idMesa: this.mesaSeleccionada.idMesa,
      fechaHora: this.eventoDetalle.fechaInicio,
      idEvento: this.eventoDetalle.idEvento,
      observaciones: `Reserva para ${this.cantidadPersonas} persona(s).`,
    };

    console.log('📝 Enviando reserva de evento:', reservaRequest);

    this.reservaService.crearReserva(reservaRequest).subscribe({
      next: (response) => {
        console.log('✅ Reserva creada:', response);

        if (response.valor && response.data) {
          const idReserva = response.data;

          if (this.metodoPago === 'YAPE' || this.metodoPago === 'PLIN') {
            const pagoRequest: CrearPagoRequest = {
              idReserva: idReserva,
              idUsuario: this.idUsuario!,
              tipoPago: 'DR',
              monto: parseFloat(this.calcularTotal()),
              metodoPago: this.metodoPago,
              observaciones: `Depósito - ${this.metodoPago}`,
            };

            console.log('💳 Enviando pago:', pagoRequest);

            this.pagoService.crearPago(pagoRequest).subscribe({
              next: (pago) => {
                console.log('✅ Pago creado:', pago);
                this.procesandoPago = false;
                this.mostrarAlertaExito();
                localStorage.removeItem('reserva_evento_temporal');
                setTimeout(() => {
                  this.mostrarAlerta = false;
                  setTimeout(
                    () => this.router.navigate(['/cliente/inicio']),
                    300,
                  );
                }, 3000);
              },
              error: (err) => {
                console.error('❌ Error al crear pago:', err);
                this.procesandoPago = false;
                this.mostrarAlertaError(
                  err.message || 'Error al procesar el pago',
                );
              },
            });
          } else {
            this.procesandoPago = false;
            this.mostrarAlertaExito();
            localStorage.removeItem('reserva_evento_temporal');
            setTimeout(() => {
              this.mostrarAlerta = false;
              setTimeout(() => this.router.navigate(['/cliente/inicio']), 300);
            }, 3000);
          }
        } else {
          this.procesandoPago = false;
          this.mostrarAlertaError(
            response.mensaje || 'Error al procesar la reserva',
          );
        }
      },
      error: (error) => {
        console.error('❌ Error al crear reserva:', error);
        this.procesandoPago = false;
        this.mostrarAlertaError(
          error.error?.mensaje || 'Error al procesar la reserva',
        );
      },
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;
    const reader = new FileReader();
    reader.onload = (e) => {
      this.imagenPreview = e.target?.result as string;
    };
    reader.readAsDataURL(input.files[0]);
  }

  intentarNuevamente(): void {
    this.imagenPreview = null;
    this.errorPago = null;
  }

  formatearNumeroTarjeta(event: Event): void {
    const input = event.target as HTMLInputElement;
    const val = input.value.replace(/\D/g, '').substring(0, 16);
    const grupos = val.match(/.{1,4}/g);
    this.datosTarjeta.numero = grupos ? grupos.join(' ') : val;
  }

  formatearFechaExpiracion(event: Event): void {
    const input = event.target as HTMLInputElement;
    let val = input.value.replace(/\D/g, '');
    if (val.length >= 2) val = val.substring(0, 2) + '/' + val.substring(2, 4);
    this.datosTarjeta.fechaExpiracion = val;
  }

  calcularTotal(): string {
    return (15 * this.cantidadPersonas).toFixed(2);
  }

  getEmojiTematica(tematica: string | undefined): string {
    if (!tematica) return '🎭';
    const t = tematica.toLowerCase();
    if (t.includes('anime')) return '⛩️';
    if (t.includes('pelicul') || t.includes('cine')) return '🎬';
    if (t.includes('comic') || t.includes('héroe') || t.includes('marvel'))
      return '🦸';
    if (t.includes('infantil') || t.includes('pokemon')) return '🎈';
    if (t.includes('videojuego') || t.includes('gamer')) return '🎮';
    if (t.includes('serie')) return '📺';
    if (t.includes('cultural')) return '🎨';
    if (t.includes('manwha') || t.includes('bl')) return '📖';
    return '🎭';
  }

  getSeats(capacidad: number): number[] {
    return Array.from({ length: capacidad }, (_, i) => i + 1);
  }

  // ─────────────────────────────────────────────────────────────────
  // HELPERS DE FECHA
  // ─────────────────────────────────────────────────────────────────

  /**
   * Convierte un ISO "YYYY-MM-DDTHH:mm:ss" del backend en un Date local.
   * El backend envía la fecha sin información de zona horaria, así que
   * la tratamos como si fuera la hora tal cual viene del servidor.
   */
  parseFecha(iso: string | null | undefined): Date | null {
    if (!iso) return null;
    const [datePart, timePart = '00:00:00'] = iso.split('T');
    const parts = datePart.split('-').map(Number);
    const times = timePart.split(':').map(Number);
    if (parts.length < 3 || isNaN(parts[0])) return null;
    return new Date(
      parts[0],
      parts[1] - 1,
      parts[2],
      times[0] ?? 0,
      times[1] ?? 0,
      times[2] ?? 0,
    );
  }

  /** "Lunes 15 de marzo de 2026" */
  formatFechaLarga(iso: string | null | undefined): string {
    const d = this.parseFecha(iso);
    if (!d) return '—';
    const dias = [
      'domingo',
      'lunes',
      'martes',
      'miércoles',
      'jueves',
      'viernes',
      'sábado',
    ];
    const meses = [
      'enero',
      'febrero',
      'marzo',
      'abril',
      'mayo',
      'junio',
      'julio',
      'agosto',
      'septiembre',
      'octubre',
      'noviembre',
      'diciembre',
    ];
    const txt = `${dias[d.getDay()]} ${d.getDate()} de ${meses[d.getMonth()]} de ${d.getFullYear()}`;
    return txt.charAt(0).toUpperCase() + txt.slice(1);
  }

  /** "18:00" */
  formatHora(iso: string | null | undefined): string {
    const d = this.parseFecha(iso);
    if (!d) return '—';
    return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`;
  }

  /** "15 mar 2026" */
  formatFechaCorta(iso: string | null | undefined): string {
    const d = this.parseFecha(iso);
    if (!d) return '—';
    const meses = [
      'ene',
      'feb',
      'mar',
      'abr',
      'may',
      'jun',
      'jul',
      'ago',
      'sep',
      'oct',
      'nov',
      'dic',
    ];
    return `${d.getDate()} ${meses[d.getMonth()]} ${d.getFullYear()}`;
  }

  // ─────────────────────────────────────────────────────────────────
  // ALERTAS
  // ─────────────────────────────────────────────────────────────────

  private mostrarAlertaExito(): void {
    this.alertaTipo = 'exito';
    this.alertaDatos = {};
    this.mostrarAlerta = true;
  }

  private mostrarAlertaError(mensaje: string): void {
    this.alertaTipo = 'error';
    this.alertaDatos = { mensaje: mensaje };
    this.mostrarAlerta = true;

    setTimeout(() => {
      this.mostrarAlerta = false;
    }, 4000);
  }
}
