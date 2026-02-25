// crudEventos/evento-form/evento-form.component.ts

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { EventoService } from '../../service/evento.service';
import { EventoRequest, TematicaResponse } from '../../models/evento.model';

@Component({
  selector: 'app-evento-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './evento.form.component.html',
  styleUrl: './evento.form.component.css'
})
export class EventoFormComponent implements OnInit {
  eventoForm: FormGroup;
  tematicas: TematicaResponse[] = [];
  isEditMode: boolean = false;
  eventoId: number | null = null;
  loading: boolean = false;
  error: string = '';
  imagenPreview: string | null = null;
  imagenBase64: string | null = null;
  archivoSeleccionado: File | null = null;

  constructor(
    private fb: FormBuilder,
    private eventoService: EventoService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.eventoForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      descripcion: ['', [Validators.maxLength(500)]],
      idTematica: [null],
      fechaInicio: ['', [Validators.required]],
      fechaFin: ['', [Validators.required]],
      costo: [0, [Validators.required, Validators.min(0.01)]],
      
    });
  }

  ngOnInit(): void {
    this.cargarTematicas();
    
    // Verificar si es modo edición
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.eventoId = +params['id'];
        this.cargarEvento(this.eventoId);
      }
    });
  }

  cargarTematicas(): void {
    this.eventoService.listarTematicas().subscribe({
      next: (data) => {
        this.tematicas = data;
      },
      error: (err) => {
        console.error('Error al cargar temáticas:', err);
      }
    });
  }

  cargarEvento(id: number): void {
    this.loading = true;
    this.eventoService.buscarPorId(id).subscribe({
      next: (evento) => {
        // Convertir fecha de ISO a formato datetime-local
        const fechaLocal = this.convertirADateTimeLocal(evento.fechaInicio);
        const fechaFinLocal = this.convertirADateTimeLocal(evento.fechaFin);
        this.eventoForm.patchValue({
          nombre: evento.nombre,
          descripcion: evento.descripcion,
          idTematica: evento.tematica?.idTematica || null,
          fecha: fechaLocal,
          costo: evento.costo,
          imagen: evento.imagen
        });

        if (evento.imagen) {
          this.imagenPreview = evento.imagen;
        }
        this.loading = false;
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

    // Validar tamaño (5MB)
    if (file.size > 5 * 1024 * 1024) {
      this.error = 'La imagen no puede superar 5MB';
      return;
    }

    this.archivoSeleccionado = file;
    this.error = '';

    // Leer archivo y crear preview
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.imagenPreview = e.target.result;
      this.imagenBase64 = e.target.result;
    };
    reader.readAsDataURL(file);
  }

  removerImagen(): void {
    this.imagenPreview = null;
    this.imagenBase64 = null;
    this.archivoSeleccionado = null;
  }

  onSubmit(): void {
    if (this.eventoForm.invalid) {
      this.marcarCamposComoTocados();
      return;
    }

    this.loading = true;
    this.error = '';

    const eventoData: EventoRequest = {
      nombre: this.eventoForm.value.nombre,
      descripcion: this.eventoForm.value.descripcion,
      idTematica: this.eventoForm.value.idTematica,
      fechaInicio: this.convertirAISO(this.eventoForm.value.fechaInicio),
      fechaFin: this.convertirAISO(this.eventoForm.value.fechaFin),
      costo: this.eventoForm.value.costo,
      imagenBase64: this.imagenBase64 || undefined
    };

    console.log('📤 Enviando evento:', {
      nombre: eventoData.nombre,
      tieneImagen: !!eventoData.imagenBase64
    });


    const request = this.isEditMode
      ? this.eventoService.actualizar(this.eventoId!, eventoData)
      : this.eventoService.crear(eventoData);

    request.subscribe({
      next: () => {
        const mensaje = this.isEditMode 
          ? 'Evento actualizado exitosamente' 
          : 'Evento creado exitosamente';
        alert(mensaje);
        this.router.navigate(['/admin/eventos']);
      },
      error: (err) => {
        this.error = err.message;
        this.loading = false;
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/admin/eventos']);
  }

  private marcarCamposComoTocados(): void {
    Object.keys(this.eventoForm.controls).forEach(key => {
      this.eventoForm.get(key)?.markAsTouched();
    });
  }

  private convertirADateTimeLocal(isoString: string): string {
    const date = new Date(isoString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    return `${year}-${month}-${day}T${hours}:${minutes}`;
  }

  private convertirAISO(dateTimeLocal: string): string {
    return new Date(dateTimeLocal).toISOString();
  }

  // Helpers para validaciones en el template
  get nombre() { return this.eventoForm.get('nombre'); }
  get descripcion() { return this.eventoForm.get('descripcion'); }
  get fecha() { return this.eventoForm.get('fecha'); }
  get costo() { return this.eventoForm.get('costo'); }
}