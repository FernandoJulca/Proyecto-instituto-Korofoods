import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  MesaItem,
  MesaSelectorComponent,
} from './mesa-selector/mesa-selector.component';
import { MesaDto } from '../../shared/dto/MesaDto';
import { MesasServiceService } from '../service/mesas-service.service';
import { ReservaServiceService } from '../service/reserva-service.service';
import { Zona } from '../../shared/enums/Zona';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ReservaRequest } from '../../shared/request/ReservaRequest';

import { AuthService } from '../../auth/service/auth.service';
import { PagoService } from '../service/pago.service';
import { CrearPagoRequest, SubirCapturaRequest } from '../pago/pagoDto';

interface CalendarDay {
  day: number;
  enabled: boolean;
  selected: boolean;
  otherMonth: boolean;
  date: Date;
}

interface TimeSlot {
  time: string;
  unavailable?: boolean;
  dateTime?: string;
}

@Component({
  selector: 'app-reserva',
  standalone: true,
  imports: [CommonModule, MesaSelectorComponent, FormsModule],
  templateUrl: './reserva.component.html',
  styleUrl: './reserva.component.css',
})
export class ReservaComponent implements OnInit {
  currentStep: number = 1;

  qrImagenUrl: string = '';
  // Paso 1: Personas
  personas: number = 1;
  quickNumbers: number[] = [1, 2, 3, 4];

  // Paso 2: Mesa
  zonas: string[] = ['Z1', 'Z2', 'Z3', 'Z4'];
  zonaSeleccionada: string = 'Z1';
  mesasDisponibles: MesaDto[] = [];
  mesaSeleccionada: MesaDto | null = null;
  cargandoMesas: boolean = false;
  mensajeMesas: string = '';

  // Paso 3: Fecha
  currentMonth: Date = new Date();
  weekDays: string[] = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];
  calendarDays: CalendarDay[] = [];
  selectedDate: Date | null = null;

  // Paso 4: Hora
  availableTimes: TimeSlot[] = [];
  cargandoSlots: boolean = false;
  mensajeSlots: string = '';
  alternativeTimes: TimeSlot[] = [];
  selectedTime: TimeSlot | null = null;

  // Paso 5: Autenticación y Métodos de pago
  usuarioAutenticado: boolean = false;
  idUsuario: number | null = null;

  metodoPagoSeleccionado: 'TARJETA' | 'YAPE' | 'PLIN' | null = null;

  // Tarjeta
  datosTarjeta = {
    numero: '',
    nombre: '',
    fechaExpiracion: '',
    cvv: '',
  };
  erroresTarjeta = {
    numero: '',
    nombre: '',
    fechaExpiracion: '',
    cvv: '',
  };
  tarjetaValida: boolean = false;

  // Captura de pago
  pagoCreado: any = null;
  imagenPreview: string | null = null;
  imagenBase64: string | null = null;
  errorPago: string = '';

  // QRs estáticos
  qrYapeUrl: string = 'assets/yape-qr.jpeg';
  qrPlinUrl: string = 'assets/plin-qr.jpeg';

  // Plin - Número ficticio
  numeroPlin: string = '986425458';

  // AGREGAR ESTADOS
  loading: boolean = false;
  depositoRequerido: number = 15.0;

  // Estado de procesamiento
  procesandoPago: boolean = false;

  mostrarAlerta: boolean = false;
  alertaTipo: 'exito' | 'error' = 'exito';
  alertaDatos = {
    idReserva: 0,
    numeroMesa: 0,
    fecha: '',
    hora: '',
    mensaje: '',
  };

  constructor(
    private mesasService: MesasServiceService,

    private reservaService: ReservaServiceService,
    private authService: AuthService,
    private router: Router,
    private pagoService: PagoService,
  ) {}

  ngOnInit(): void {
    this.generateCalendar();
    this.verificarSesion();
  }

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

  irALogin(): void {
    const reservaTemp = {
      personas: this.personas,
      mesa: this.mesaSeleccionada,
      fecha: this.selectedDate,
      hora: this.selectedTime,
    };
    localStorage.setItem('reserva_temporal', JSON.stringify(reservaTemp));

    this.router.navigate(['/login']);
  }

  irARegistro(): void {
    const reservaTemp = {
      personas: this.personas,
      mesa: this.mesaSeleccionada,
      fecha: this.selectedDate,
      hora: this.selectedTime,
    };
    localStorage.setItem('reserva_temporal', JSON.stringify(reservaTemp));

    this.router.navigate(['/register']);
  }

  // Navegación de pasos
  nextStep(): void {
    if (this.currentStep === 1 && this.personas > 0) {
      this.currentStep++;
      this.cargarMesasPorZona();
    } else if (this.currentStep === 2 && this.mesaSeleccionada) {
      this.currentStep++;
    } else if (this.currentStep === 3 && this.selectedDate) {
      this.currentStep++;
      this.cargarSlotsDisponibles();
    } else if (this.currentStep === 4 && this.selectedTime) {
      this.currentStep++;
      this.verificarSesion();
    }
  }

  previousStep(): void {
    if (this.currentStep > 1) {
      this.currentStep--;
    }
  }

  // Paso 1: Personas
  incrementPersonas(): void {
    if (this.personas < 20) {
      this.personas++;
    }
  }

  decrementPersonas(): void {
    if (this.personas > 1) {
      this.personas--;
    }
  }

  setPersonas(num: number): void {
    this.personas = num;
  }

  // Paso 2: Mesas
  seleccionarZona(zona: string): void {
    this.zonaSeleccionada = zona;
    this.mesaSeleccionada = null;
    this.cargarMesasPorZona();
  }

  cargarMesasPorZona(): void {
    this.cargandoMesas = true;
    this.mesasService
      .obtenerMesasPorZona(this.zonaSeleccionada as Zona, this.personas)
      .subscribe({
        next: (response) => {
          console.log('✅ Response completo:', response);
          this.mesasDisponibles = response.data;
          this.mensajeMesas = response.mensaje!;
          this.cargandoMesas = false;
        },
        error: (error) => {
          console.error('❌ Error al cargar mesas:', error);
          this.mesasDisponibles = [];
          this.mensajeMesas = 'Error al cargar las mesas disponibles';
          this.cargandoMesas = false;
        },
      });
  }

  onMesaSeleccionada(mesa: MesaItem): void {
    if ('estado' in mesa && 'tipo' in mesa) {
      this.mesaSeleccionada = mesa as MesaDto;
      console.log('Mesa seleccionada:', mesa);
    }
  }

  // Paso 3: Calendario
  generateCalendar(): void {
    const year = this.currentMonth.getFullYear();
    const month = this.currentMonth.getMonth();

    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const prevLastDay = new Date(year, month, 0);

    const firstDayOfWeek = firstDay.getDay();
    const lastDateOfMonth = lastDay.getDate();
    const prevLastDate = prevLastDay.getDate();

    this.calendarDays = [];

    // Días del mes anterior
    for (let i = firstDayOfWeek - 1; i >= 0; i--) {
      this.calendarDays.push({
        day: prevLastDate - i,
        enabled: false,
        selected: false,
        otherMonth: true,
        date: new Date(year, month - 1, prevLastDate - i),
      });
    }

    // Días del mes actual
    for (let i = 1; i <= lastDateOfMonth; i++) {
      const date = new Date(year, month, i);
      const isPast = date < new Date(new Date().setHours(0, 0, 0, 0));

      this.calendarDays.push({
        day: i,
        enabled: !isPast,
        selected: this.selectedDate?.toDateString() === date.toDateString(),
        otherMonth: false,
        date: date,
      });
    }

    // Días del siguiente mes
    const remainingDays = 42 - this.calendarDays.length;
    for (let i = 1; i <= remainingDays; i++) {
      this.calendarDays.push({
        day: i,
        enabled: false,
        selected: false,
        otherMonth: true,
        date: new Date(year, month + 1, i),
      });
    }
  }

  previousMonth(): void {
    this.currentMonth = new Date(
      this.currentMonth.getFullYear(),
      this.currentMonth.getMonth() - 1,
    );
    this.generateCalendar();
  }

  nextMonth(): void {
    this.currentMonth = new Date(
      this.currentMonth.getFullYear(),
      this.currentMonth.getMonth() + 1,
    );
    this.generateCalendar();
  }

  getMonthYear(): string {
    const months = [
      'Enero',
      'Febrero',
      'Marzo',
      'Abril',
      'Mayo',
      'Junio',
      'Julio',
      'Agosto',
      'Septiembre',
      'Octubre',
      'Noviembre',
      'Diciembre',
    ];
    return `${months[this.currentMonth.getMonth()]} ${this.currentMonth.getFullYear()}`;
  }

  selectDate(day: CalendarDay): void {
    if (!day.enabled || day.otherMonth) return;
    this.selectedDate = day.date;
    this.generateCalendar();
  }

  formatDate(date: Date): string {
    const days = ['dom', 'lun', 'mar', 'mié', 'jue', 'vie', 'sáb'];
    const months = [
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

    return `${days[date.getDay()]}, ${date.getDate()} ${months[date.getMonth()]}`;
  }

  // Paso 4: Cargar slots disponibles
  cargarSlotsDisponibles(): void {
    if (!this.mesaSeleccionada || !this.selectedDate) {
      console.error('❌ Falta mesa o fecha seleccionada');
      return;
    }

    this.cargandoSlots = true;
    this.availableTimes = [];
    this.selectedTime = null;

    // Construir desde (fecha seleccionada a las 12:00:00)
    const desde = new Date(this.selectedDate);
    desde.setHours(12 - 5, 0, 0, 0);

    // Construir hasta (fecha seleccionada a las 23:00:00)
    const hasta = new Date(this.selectedDate);
    hasta.setHours(23 - 5, 0, 0, 0);

    const desdeISO = desde.toISOString();
    const hastaISO = hasta.toISOString();

    this.reservaService
      .obtenerSlotsDisponibles(
        this.mesaSeleccionada.idMesa!,
        desdeISO,
        hastaISO,
      )
      .subscribe({
        next: (response) => {
          if (response.data && response.data.length > 0) {
            this.availableTimes = response.data.map((dateTimeStr) => {
              const date = new Date(dateTimeStr);
              const hours = date.getHours().toString().padStart(2, '0');
              const minutes = date.getMinutes().toString().padStart(2, '0');

              return {
                time: `${hours}:${minutes}`,
                unavailable: false,
                dateTime: dateTimeStr,
              };
            });

            this.mensajeSlots = response.mensaje || 'Slots cargados';
          } else {
            this.availableTimes = [];
            this.mensajeSlots = 'No hay horarios disponibles para esta fecha';
          }

          this.cargandoSlots = false;
        },
        error: (error) => {
          console.error('❌ Error al cargar slots:', error);
          this.availableTimes = [];
          this.mensajeSlots = 'Error al cargar horarios disponibles';
          this.cargandoSlots = false;
        },
      });
  }

  selectTime(time: TimeSlot): void {
    if (time.unavailable) return;
    this.selectedTime = time;
  }

  // Paso 5: Métodos de pago
  seleccionarMetodoPago(metodo: 'TARJETA' | 'YAPE' | 'PLIN'): void {
    this.metodoPagoSeleccionado = metodo;
    this.limpiarErroresTarjeta();

    if (metodo === 'TARJETA' && this.datosTarjeta.numero) {
      setTimeout(() => {
        this.validarNumeroTarjeta();
        this.validarNombreTitular();
        this.validarFechaExpiracion();
        this.validarCVV();
      }, 100);
    }
  }

  validarNumeroTarjeta(): void {
    const numero = this.datosTarjeta.numero.replace(/\s/g, '');

    if (numero.length === 0) {
      this.erroresTarjeta.numero = 'El número de tarjeta es requerido';
    } else if (!/^\d+$/.test(numero)) {
      this.erroresTarjeta.numero = 'Solo se permiten números';
    } else if (numero.length < 13 || numero.length > 19) {
      this.erroresTarjeta.numero = 'Número de tarjeta inválido (13-19 dígitos)';
    } else {
      this.erroresTarjeta.numero = '';
    }

    this.verificarTarjetaValida();
  }

  validarNombreTitular(): void {
    const nombre = this.datosTarjeta.nombre.trim();

    if (nombre.length === 0) {
      this.erroresTarjeta.nombre = 'El nombre del titular es requerido';
    } else if (nombre.length < 3) {
      this.erroresTarjeta.nombre = 'El nombre debe tener al menos 3 caracteres';
    } else if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/.test(nombre)) {
      this.erroresTarjeta.nombre = 'Solo se permiten letras y espacios';
    } else {
      this.erroresTarjeta.nombre = '';
    }

    this.verificarTarjetaValida();
  }

  validarFechaExpiracion(): void {
    const fecha = this.datosTarjeta.fechaExpiracion;

    if (fecha.length === 0) {
      this.erroresTarjeta.fechaExpiracion =
        'La fecha de expiración es requerida';
    } else if (!/^\d{2}\/\d{2}$/.test(fecha)) {
      this.erroresTarjeta.fechaExpiracion = 'Formato inválido (MM/AA)';
    } else {
      const [mes, anio] = fecha.split('/').map(Number);
      const anioCompleto = 2000 + anio;
      const fechaExpiracion = new Date(anioCompleto, mes - 1);
      const hoy = new Date();

      if (mes < 1 || mes > 12) {
        this.erroresTarjeta.fechaExpiracion = 'Mes inválido (01-12)';
      } else if (fechaExpiracion < hoy) {
        this.erroresTarjeta.fechaExpiracion = 'Tarjeta expirada';
      } else {
        this.erroresTarjeta.fechaExpiracion = '';
      }
    }

    this.verificarTarjetaValida();
  }

  validarCVV(): void {
    const cvv = this.datosTarjeta.cvv;

    if (cvv.length === 0) {
      this.erroresTarjeta.cvv = 'El CVV es requerido';
    } else if (!/^\d{3,4}$/.test(cvv)) {
      this.erroresTarjeta.cvv = 'CVV inválido (3-4 dígitos)';
    } else {
      this.erroresTarjeta.cvv = '';
    }

    this.verificarTarjetaValida();
  }

  formatearNumeroTarjeta(event: any): void {
    let valor = event.target.value.replace(/\s/g, '');
    let valorFormateado = '';

    for (let i = 0; i < valor.length && i < 16; i++) {
      if (i > 0 && i % 4 === 0) {
        valorFormateado += ' ';
      }
      valorFormateado += valor[i];
    }

    this.datosTarjeta.numero = valorFormateado;
    this.validarNumeroTarjeta();
  }

  formatearFechaExpiracion(event: any): void {
    let valor = event.target.value.replace(/\D/g, '');

    if (valor.length >= 2) {
      valor = valor.substring(0, 2) + '/' + valor.substring(2, 4);
    }

    this.datosTarjeta.fechaExpiracion = valor;
    this.validarFechaExpiracion();
  }

  soloNumeros(event: KeyboardEvent): boolean {
    const charCode = event.which ? event.which : event.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
      event.preventDefault();
      return false;
    }
    return true;
  }

  private verificarTarjetaValida(): void {
    this.tarjetaValida =
      this.datosTarjeta.numero.replace(/\s/g, '').length >= 13 &&
      this.datosTarjeta.nombre.trim().length >= 3 &&
      this.datosTarjeta.fechaExpiracion.length === 5 &&
      this.datosTarjeta.cvv.length >= 3 &&
      !this.erroresTarjeta.numero &&
      !this.erroresTarjeta.nombre &&
      !this.erroresTarjeta.fechaExpiracion &&
      !this.erroresTarjeta.cvv;
  }

  private limpiarErroresTarjeta(): void {
    this.erroresTarjeta = {
      numero: '',
      nombre: '',
      fechaExpiracion: '',
      cvv: '',
    };
  }

  limpiarFormularioTarjeta(): void {
    this.datosTarjeta = {
      numero: '',
      nombre: '',
      fechaExpiracion: '',
      cvv: '',
    };
    this.limpiarErroresTarjeta();
    this.tarjetaValida = false;
  }

  copiarNumeroPlin(): void {
    navigator.clipboard
      .writeText(this.numeroPlin.replace(/\s/g, ''))
      .then(() => {
        alert('Número copiado al portapapeles');
      })
      .catch(() => {
        alert('Error al copiar el número');
      });
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      alert('Solo se permiten imágenes');
      return;
    }

    if (file.size > 10 * 1024 * 1024) {
      alert('La imagen no puede superar 10MB');
      return;
    }

    this.errorPago = '';

    // Leer imagen
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.imagenPreview = e.target.result;
      this.imagenBase64 = e.target.result;
    };
    reader.readAsDataURL(file);
  }

  intentarNuevamente(): void {
    this.imagenPreview = null;
    this.imagenBase64 = null;
    this.errorPago = '';
    this.procesandoPago = false;
  }

  construirFechaReservaISO(): string {
    if (!this.selectedDate || !this.selectedTime) {
      throw new Error('Fecha u hora no seleccionada');
    }

    // Usar el dateTime del slot seleccionado
    if (this.selectedTime.dateTime) {
      return this.selectedTime.dateTime;
    }

    const fecha = new Date(this.selectedDate);
    const [hora, minuto] = this.selectedTime.time.split(':');
    fecha.setHours(parseInt(hora), parseInt(minuto), 0, 0);
    return fecha.toISOString();
  }

  puedeConfirmarPago(): boolean {
    if (!this.usuarioAutenticado || !this.idUsuario) {
      return false;
    }

    if (!this.metodoPagoSeleccionado) {
      return false;
    }

    if (this.metodoPagoSeleccionado === 'TARJETA') {
      return this.tarjetaValida;
    }

    if (
      this.metodoPagoSeleccionado === 'YAPE' ||
      this.metodoPagoSeleccionado === 'PLIN'
    ) {
      return this.imagenBase64 !== null;
    }

    return false;
  }

  confirmarPago(): void {
    if (!this.puedeConfirmarPago()) {
      alert('Por favor completa todos los datos');
      return;
    }

    if (!this.usuarioAutenticado || !this.idUsuario) {
      alert('Debe iniciar sesión para completar la reserva');
      return;
    }

    if (
      this.metodoPagoSeleccionado === 'YAPE' ||
      this.metodoPagoSeleccionado === 'PLIN'
    ) {
      this.procesarPagoConCaptura();
    } else {
      // TARJETA: solo crear reserva, sin llamar a crearPago
      this.procesandoPago = true;
      setTimeout(() => {
        this.crearReserva();
      }, 2000);
    }
  }

  procesarPagoConCaptura(): void {
    if (!this.imagenBase64) {
      alert('Debes subir el comprobante de pago');
      return;
    }

    if (!this.metodoPagoSeleccionado) {
      alert('Debes seleccionar un método de pago');
      return;
    }

    if (!this.idUsuario) {
      alert('Error: No se pudo obtener el ID de usuario');
      this.router.navigate(['/login']);
      return;
    }

    this.procesandoPago = true;
    this.errorPago = '';

    const reservaRequest: ReservaRequest = {
      idUsuario: this.idUsuario,
      idMesa: this.mesaSeleccionada!.idMesa!,
      fechaHora: this.construirFechaReservaISO(),
      idEvento: null,
      observaciones: `Reserva para ${this.personas} personas`,
    };

    console.log('📝 Creando reserva:', reservaRequest);

    this.reservaService.crearReserva(reservaRequest).subscribe({
      next: (resultReserva) => {
        console.log('✅ Reserva creada:', resultReserva);

        if (resultReserva.valor && resultReserva.data) {
          const idReserva = resultReserva.data;
          const metodoPago = this.metodoPagoSeleccionado!;
          const idUsuario = this.idUsuario!;

          const pagoRequest: CrearPagoRequest = {
            idReserva: idReserva,
            idUsuario: idUsuario,
            tipoPago: 'DR',
            monto: 15.0,
            metodoPago: metodoPago,
            observaciones: `Depósito - ${metodoPago}`,
          };

          console.log('💳 Creando pago:', pagoRequest);

          this.pagoService.crearPago(pagoRequest).subscribe({
            next: (pago) => {
              this.pagoCreado = pago;
              this.procesandoPago = false;
              console.log('✅ Pago creado:', pago);

              this.mostrarAlertaExito(
                idReserva,
                this.mesaSeleccionada?.numeroMesa,
                this.formatDate(this.selectedDate!),
                this.selectedTime?.time,
              );

              setTimeout(() => this.router.navigate(['/cliente/inicio']), 3000);
            },
            error: (err) => {
              this.procesandoPago = false;
              alert('Error al crear pago: ' + err.message);
              console.error('❌ Error al crear pago:', err);
            },
          });
        } else {
          this.procesandoPago = false;
          this.mostrarAlertaError(
            resultReserva.mensaje || 'Error al procesar la reserva',
          );
        }
      },
      error: (err) => {
        this.procesandoPago = false;
        alert('Error al crear reserva: ' + err.message);
        console.error('❌ Error al crear reserva:', err);
      },
    });
  }

  getMesaNumero(): string {
    return this.mesaSeleccionada
      ? `#${this.mesaSeleccionada.numeroMesa}`
      : 'No seleccionada';
  }

  getMesaZona(): string {
    return this.mesaSeleccionada ? this.mesaSeleccionada.tipo : '-';
  }

  private crearReserva(): void {
    if (!this.mesaSeleccionada || !this.selectedTime || !this.idUsuario) {
      console.error('Faltan datos para crear la reserva');
      this.procesandoPago = false;
      return;
    }

    const reservaRequest: ReservaRequest = {
      idUsuario: this.idUsuario,
      idMesa: this.mesaSeleccionada.idMesa!,
      fechaHora: this.selectedTime.dateTime!,
      idEvento: null,
      observaciones: `Reserva para ${this.personas} persona(s).`,
    };

    console.log('📝 Enviando reserva (TARJETA):', reservaRequest);

    this.reservaService.crearReserva(reservaRequest).subscribe({
      next: (response) => {
        console.log('✅ Reserva creada exitosamente:', response);
        this.procesandoPago = false;

        this.mostrarAlertaExito(
          response.data,
          this.mesaSeleccionada?.numeroMesa,
          this.formatDate(this.selectedDate!),
          this.selectedTime?.time,
        );

        localStorage.removeItem('reserva_temporal');

        setTimeout(() => {
          this.router.navigate(['/cliente/inicio']);
        }, 3000);
      },
      error: (error) => {
        console.error('❌ Error al crear reserva:', error);
        this.procesandoPago = false;
        const mensaje = error.error?.mensaje || 'Error al procesar la reserva';
        this.mostrarAlertaError(mensaje);
      },
    });
  }

  private mostrarAlertaExito(
    idReserva: number,
    numeroMesa: number | undefined,
    fecha: string,
    hora: string | undefined,
  ): void {
    this.alertaTipo = 'exito';
    this.alertaDatos = {
      idReserva: idReserva,
      numeroMesa: numeroMesa || 0,
      fecha: fecha,
      hora: hora || '',
      mensaje: '',
    };
    this.mostrarAlerta = true;

    setTimeout(() => {
      this.mostrarAlerta = false;
      setTimeout(() => {
        this.router.navigate(['/cliente/inicio']);
      }, 300);
    }, 3000);
  }

  private mostrarAlertaError(mensaje: string): void {
    this.alertaTipo = 'error';
    this.alertaDatos = {
      idReserva: 0,
      numeroMesa: 0,
      fecha: '',
      hora: '',
      mensaje: mensaje,
    };
    this.mostrarAlerta = true;

    setTimeout(() => {
      this.mostrarAlerta = false;
    }, 4000);
  }
}

