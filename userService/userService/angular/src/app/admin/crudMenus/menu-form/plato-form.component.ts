
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PlatoService } from '../../service/plato.service';
import { PlatoRequest, TIPOS_PLATO, EtiquetaResponse } from '../../models/plato.model';

@Component({
  selector: 'app-plato-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './plato-form.component.html',
  styleUrl: './plato-form.component.css'
})
export class PlatoFormComponent implements OnInit {
  platoForm: FormGroup;
  etiquetas: EtiquetaResponse[] = [];
  tiposPlato = TIPOS_PLATO;
  isEditMode: boolean = false;
  platoId: number | null = null;
  loading: boolean = false;
  error: string = '';

  imagenPreview: string | null = null;
  imagenBase64: string | null = null;
  archivoSeleccionado: File | null = null;

  constructor(
    private fb: FormBuilder,
    private platoService: PlatoService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.platoForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      precio: [0, [Validators.required, Validators.min(0.01)]],
      stock: [0, [Validators.required, Validators.min(0)]],
      tipoPlato: ['', [Validators.required]],
      
    });
  }

  ngOnInit(): void {
    this.cargarEtiquetas();
    
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.platoId = +params['id'];
        this.cargarPlato(this.platoId);
      }
    });
  }

  cargarEtiquetas(): void {
    this.platoService.listarEtiquetas().subscribe({
      next: (data) => {
        this.etiquetas = data;
      },
      error: (err) => {
        console.error('Error al cargar etiquetas:', err);
      }
    });
  }

  cargarPlato(id: number): void {
    this.loading = true;
    this.platoService.buscarPorId(id).subscribe({
      next: (plato) => {
        this.platoForm.patchValue({
          nombre: plato.nombre,
          precio: plato.precio,
          stock: plato.stock,
          tipoPlato: plato.tipoPlato,
          imagen: plato.imagen
        });

        if (plato.imagen) {
          this.imagenPreview = plato.imagen;
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
      this.imagenBase64 = e.target.result; // Incluye data:image/...
    };
    reader.readAsDataURL(file);
  }

  removerImagen(): void {
    this.imagenPreview = null;
    this.imagenBase64 = null;
    this.archivoSeleccionado = null;
  }

  onSubmit(): void {
    if (this.platoForm.invalid) {
      this.marcarCamposComoTocados();
      return;
    }

    this.loading = true;
    this.error = '';

    const platoData: PlatoRequest = {
      ...this.platoForm.value,
      imagenBase64: this.imagenBase64 || undefined // Solo si hay imagen nueva
    };

    console.log('📤 Enviando plato:', {
      nombre: platoData.nombre,
      tieneImagen: !!platoData.imagenBase64
    });

    const request = this.isEditMode
      ? this.platoService.actualizar(this.platoId!, platoData)
      : this.platoService.crear(platoData);

    request.subscribe({
      next: () => {
        const mensaje = this.isEditMode 
          ? 'Plato actualizado exitosamente' 
          : 'Plato creado exitosamente';
        alert(mensaje);
        this.router.navigate(['/admin/menus']);
      },
      error: (err) => {
        this.error = err.message;
        this.loading = false;
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/admin/menus']);
  }

  private marcarCamposComoTocados(): void {
    Object.keys(this.platoForm.controls).forEach(key => {
      this.platoForm.get(key)?.markAsTouched();
    });
  }

  get nombre() { return this.platoForm.get('nombre'); }
  get precio() { return this.platoForm.get('precio'); }
  get stock() { return this.platoForm.get('stock'); }
  get tipoPlato() { return this.platoForm.get('tipoPlato'); }
}