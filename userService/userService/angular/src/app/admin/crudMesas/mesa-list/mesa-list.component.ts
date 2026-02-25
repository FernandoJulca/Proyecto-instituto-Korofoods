
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MesaService } from '../../service/mesa.service';
import { MesaResponse } from '../../models/mesa.model';

@Component({
  selector: 'app-mesa-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './mesa-list.component.html',
  styleUrl: './mesa-list.component.css'
})
export class MesaListComponent implements OnInit {
  mesas: MesaResponse[] = [];
  loading: boolean = false;
  error: string = '';

  constructor(private mesaService: MesaService) {}

  ngOnInit(): void {
    this.cargarMesas();
  }

  cargarMesas(): void {
    this.loading = true;
    this.error = '';
    
    this.mesaService.listarTodas().subscribe({
      next: (data) => {
        this.mesas = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.message;
        this.loading = false;
        console.error('Error al cargar mesas:', err);
      }
    });
  }

  confirmarEliminar(mesa: MesaResponse): void {
    const confirmar = confirm(`¿Estás seguro de eliminar la mesa número ${mesa.numeroMesa}?`);
    
    if (confirmar) {
      this.eliminarMesa(mesa.idMesa);
    }
  }

  eliminarMesa(id: number): void {
    this.mesaService.eliminar(id).subscribe({
      next: () => {
        alert('Mesa eliminada exitosamente');
        this.cargarMesas();
      },
      error: (err) => {
        alert(`Error al eliminar: ${err.message}`);
        console.error('Error al eliminar mesa:', err);
      }
    });
  }

  cambiarEstadoRapido(mesa: MesaResponse, nuevoEstado: string): void {
    if (mesa.estado === nuevoEstado) {
      return; // Ya está en ese estado
    }

    this.mesaService.cambiarEstado(mesa.idMesa, nuevoEstado).subscribe({
      next: () => {
        alert(`Estado cambiado a ${nuevoEstado}`);
        this.cargarMesas();
      },
      error: (err) => {
        alert(`Error al cambiar estado: ${err.message}`);
        console.error('Error al cambiar estado:', err);
      }
    });
  }

  getBadgeClassZona(zona: string): string {
    const classes: { [key: string]: string } = {
      'Z1': 'bg-primary',
      'Z2': 'bg-info'
    };
    return classes[zona] || 'bg-secondary';
  }

  getBadgeClassEstado(estado: string): string {
    const classes: { [key: string]: string } = {
      'LIBRE': 'bg-success',
      'ASIGNADA': 'bg-warning',
      'OCUPADA': 'bg-danger'
    };
    return classes[estado] || 'bg-secondary';
  }
}