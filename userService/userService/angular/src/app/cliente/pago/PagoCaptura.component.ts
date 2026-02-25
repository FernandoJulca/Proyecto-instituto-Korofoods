// pago-captura/pago-captura.component.ts

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PagoService } from '../../cliente/service/pago.service';
import { ReservaServiceService } from '../../cliente/service/reserva-service.service';
import { 
  CrearPagoRequest, 
  SubirCapturaRequest, 
  PagoResponse, 
  EstadoPago 
} from '../../cliente/pago/pagoDto';

@Component({
  selector: 'app-pago-captura',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pago-captura.component.html',
  styleUrl: './pago-captura.component.css'
})
export class PagoCapturaComponent implements OnInit {
  // Datos de la reserva (vienen de pasos anteriores)
  idReserva: number | null = null;
  idUsuario: number = 1; // TODO: Obtener del auth
  monto: number = 15.00; // Depósito fijo
  
  // Estado del pago
  pagoCreado: PagoResponse | null = null;
  metodoPagoSeleccionado: 'YAPE' | 'PLIN' | null = null;
  
  // QRs estáticos
  qrYape: string = '../../../assets/yape-qr.jpeg';
  qrPlin: string = '../../../assets/plin-qr.jpeg';
  
  // Captura
  archivoSeleccionado: File | null = null;
  imagenPreview: string | null = null;
  imagenBase64: string | null = null;
  
  // Estados
  loading: boolean = false;
  error: string = '';
  pasoActual: 'seleccionar-metodo' | 'mostrar-qr' | 'subir-captura' | 'procesando' | 'resultado' = 'seleccionar-metodo';

  constructor(
    private pagoService: PagoService,
    private reservaService: ReservaServiceService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Obtener ID de reserva de los parámetros de ruta o estado
    this.route.queryParams.subscribe(params => {
      this.idReserva = params['idReserva'] ? +params['idReserva'] : null;
    });
  }

  seleccionarMetodo(metodo: 'YAPE' | 'PLIN'): void {
    this.metodoPagoSeleccionado = metodo;
    this.error = '';
    
    // Crear el pago en el backend
    this.crearPago(metodo);
  }

  crearPago(metodo: string): void {
    if (!this.idReserva) {
      this.error = 'No hay reserva asociada';
      return;
    }

    this.loading = true;

    const request: CrearPagoRequest = {
      idReserva: this.idReserva,
      idUsuario: this.idUsuario,
      tipoPago: 'DR', // Depósito Reserva
      monto: this.monto,
      metodoPago: metodo,
      observaciones: `Depósito para reserva - Método: ${metodo}`
    };

    this.pagoService.crearPago(request).subscribe({
      next: (pago) => {
        this.pagoCreado = pago;
        this.pasoActual = 'mostrar-qr';
        this.loading = false;
        console.log('Pago creado:', pago);
      },
      error: (err) => {
        this.error = err.message;
        this.loading = false;
      }
    });
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (!file) return;

    // Validar tipo de archivo
    if (!file.type.startsWith('image/')) {
      this.error = 'Solo se permiten imágenes';
      return;
    }

    // Validar tamaño (máx 10MB)
    if (file.size > 10 * 1024 * 1024) {
      this.error = 'La imagen no puede superar 10MB';
      return;
    }

    this.archivoSeleccionado = file;
    this.error = '';

    // Preview de la imagen
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.imagenPreview = e.target.result;
      this.imagenBase64 = e.target.result; // Ya incluye el prefijo data:image/...
    };
    reader.readAsDataURL(file);

    // Cambiar al paso de subir captura
    this.pasoActual = 'subir-captura';
  }

  subirCaptura(): void {
    if (!this.imagenBase64 || !this.pagoCreado || !this.metodoPagoSeleccionado) {
      this.error = 'Faltan datos para procesar el pago';
      return;
    }

    this.loading = true;
    this.pasoActual = 'procesando';
    this.error = '';

    const request: SubirCapturaRequest = {
      idPago: this.pagoCreado.idPago,
      imagenBase64: this.imagenBase64,
      metodoPago: this.metodoPagoSeleccionado
    };

    this.pagoService.subirCaptura(request).subscribe({
      next: (pago) => {
        this.pagoCreado = pago;
        this.loading = false;
        
        if (pago.estado === EstadoPago.PAG) {
          this.pasoActual = 'resultado';
          setTimeout(() => {
            this.router.navigate(['/reserva/confirmada', pago.idReserva]);
          }, 3000);
        } else if (pago.estado === EstadoPago.RECH) {
          this.pasoActual = 'resultado';
          this.error = pago.motivoRechazo || 'Pago rechazado';
        }
      },
      error: (err) => {
        this.loading = false;
        this.pasoActual = 'resultado';
        this.error = err.message;
      }
    });
  }

  volverASeleccionarMetodo(): void {
    this.pasoActual = 'seleccionar-metodo';
    this.metodoPagoSeleccionado = null;
    this.archivoSeleccionado = null;
    this.imagenPreview = null;
    this.imagenBase64 = null;
    this.error = '';
  }

  intentarNuevamente(): void {
    this.archivoSeleccionado = null;
    this.imagenPreview = null;
    this.imagenBase64 = null;
    this.error = '';
    this.pasoActual = 'mostrar-qr';
  }

  cancelar(): void {
    this.router.navigate(['/reservas']);
  }
}