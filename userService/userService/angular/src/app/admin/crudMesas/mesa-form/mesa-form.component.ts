
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MesaService } from '../../service/mesa.service';
import { MesaRequest, ZONAS, ESTADOS_MESA } from '../../models/mesa.model';

@Component({
  selector: 'app-mesa-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './mesa-form.component.html',
  styleUrl: './mesa-form.component.css'
})
export class MesaFormComponent implements OnInit {
  mesaForm: FormGroup;
  zonas = ZONAS;
  estados = ESTADOS_MESA;
  isEditMode: boolean = false;
  mesaId: number | null = null;
  loading: boolean = false;
  error: string = '';

  constructor(
    private fb: FormBuilder,
    private mesaService: MesaService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.mesaForm = this.fb.group({
      numeroMesa: [null, [Validators.required, Validators.min(1)]],
      capacidad: [null, [Validators.required, Validators.min(1), Validators.max(20)]],
      zona: ['', [Validators.required]],
      estado: ['LIBRE', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.mesaId = +params['id'];
        this.cargarMesa(this.mesaId);
      }
    });
  }

  cargarMesa(id: number): void {
    this.loading = true;
    this.mesaService.buscarPorId(id).subscribe({
      next: (mesa) => {
        this.mesaForm.patchValue({
          numeroMesa: mesa.numeroMesa,
          capacidad: mesa.capacidad,
          zona: mesa.zona,
          estado: mesa.estado
        });
        this.loading = false;
      },
      error: (err) => {
        this.error = err.message;
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.mesaForm.invalid) {
      this.marcarCamposComoTocados();
      return;
    }

    this.loading = true;
    this.error = '';

    const mesaData: MesaRequest = this.mesaForm.value;

    const request = this.isEditMode
      ? this.mesaService.actualizar(this.mesaId!, mesaData)
      : this.mesaService.crear(mesaData);

    request.subscribe({
      next: () => {
        const mensaje = this.isEditMode 
          ? 'Mesa actualizada exitosamente' 
          : 'Mesa creada exitosamente';
        alert(mensaje);
        this.router.navigate(['/admin/mesas']);
      },
      error: (err) => {
        this.error = err.message;
        this.loading = false;
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/admin/mesas']);
  }

  private marcarCamposComoTocados(): void {
    Object.keys(this.mesaForm.controls).forEach(key => {
      this.mesaForm.get(key)?.markAsTouched();
    });
  }

  get numeroMesa() { return this.mesaForm.get('numeroMesa'); }
  get capacidad() { return this.mesaForm.get('capacidad'); }
  get zona() { return this.mesaForm.get('zona'); }
  get estado() { return this.mesaForm.get('estado'); }
}