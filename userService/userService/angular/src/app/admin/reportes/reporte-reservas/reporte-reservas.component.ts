import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ReporteReservasRequest, ReservaServiceService } from '../../../cliente/service/reserva-service.service';


@Component({
  selector: 'app-reporte-reservas',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reporte-reservas.component.html',
  styleUrl: './reporte-reservas.component.css'
})
export class ReporteReservasComponent implements OnInit{

  reporteForm: FormGroup;
  loading: boolean = false;
  error: string = '';

  estados = [
    { value: '', label: 'Todos' },
    { value: 'PENDIENTE', label: 'Pendiente' },
    { value: 'CONFIRMADA', label: 'Confirmada' },
    { value: 'CANCELADA', label: 'Cancelada' },
    { value: 'COMPLETADA', label: 'Completada' }
  ];

  zonas = [
    { value: '', label: 'Todas' },
    { value: 'Z1', label: 'Zona 1' },
    { value: 'Z2', label: 'Zona 2' },
    { value: 'Z3', label: 'Zona 3' },
    { value: 'Z4', label: 'Zona 4' }
  ];

  constructor(
    private fb: FormBuilder,
    private reservaService: ReservaServiceService
  ) {
    this.reporteForm = this.fb.group({
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required],
      estado: [''],
      zona: ['']
    });
  }

  ngOnInit(): void {
    // Establecer fechas por defecto (último mes)
    const hoy = new Date();
    const haceUnMes = new Date();
    haceUnMes.setMonth(haceUnMes.getMonth() - 1);

    this.reporteForm.patchValue({
      fechaInicio: this.formatearFecha(haceUnMes),
      fechaFin: this.formatearFecha(hoy)
    });
  }

  generarReporte(): void {
    if (this.reporteForm.invalid) {
      this.marcarCamposComoTocados();
      return;
    }

    this.loading = true;
    this.error = '';

    const request: ReporteReservasRequest = {
      fechaInicio: this.reporteForm.value.fechaInicio,
      fechaFin: this.reporteForm.value.fechaFin,
      estado: this.reporteForm.value.estado || undefined,
      zona: this.reporteForm.value.zona || undefined
    };

    console.log('📊 Generando reporte con:', request);

    this.reservaService.generarReporteReservas(request).subscribe({
      next: (blob) => {
        this.loading = false;
        this.descargarPDF(blob);
      },
      error: (err) => {
        this.loading = false;
        this.error = 'Error al generar el reporte. Por favor, intenta nuevamente.';
        console.error('Error al generar reporte:', err);
      }
    });
  }

  private descargarPDF(blob: Blob): void {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    
    const fechaInicio = this.reporteForm.value.fechaInicio;
    const fechaFin = this.reporteForm.value.fechaFin;
    link.download = `reporte-reservas-${fechaInicio}-${fechaFin}.pdf`;
    
    link.click();
    window.URL.revokeObjectURL(url);
    
    console.log(' Reporte descargado exitosamente');
  }

  private formatearFecha(fecha: Date): string {
    const year = fecha.getFullYear();
    const month = String(fecha.getMonth() + 1).padStart(2, '0');
    const day = String(fecha.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private marcarCamposComoTocados(): void {
    Object.keys(this.reporteForm.controls).forEach(key => {
      this.reporteForm.get(key)?.markAsTouched();
    });
  }

  get estadoLabel(): string {
  const estado = this.reporteForm.value.estado;
  if (!estado) return 'Todos';

  const encontrado = this.estados.find(e => e.value === estado);
  return encontrado ? encontrado.label : 'Todos';
}

get zonaLabel(): string {
  const zona = this.reporteForm.value.zona;
  if (!zona) return 'Todas';

  const encontrado = this.zonas.find(z => z.value === zona);
  return encontrado ? encontrado.label : 'Todas';
}

  get fechaInicio() { return this.reporteForm.get('fechaInicio'); }
  get fechaFin() { return this.reporteForm.get('fechaFin'); }
}
