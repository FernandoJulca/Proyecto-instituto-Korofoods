
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PlatoService } from '../../service/plato.service';
import { PlatoResponse } from '../../models/plato.model';

@Component({
  selector: 'app-plato-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './plato-list.component.html',
  styleUrl: './plato-list.component.css'
})
export class PlatoListComponent implements OnInit {
  platos: PlatoResponse[] = [];
  loading: boolean = false;
  error: string = '';

  constructor(private platoService: PlatoService) {}

  ngOnInit(): void {
    this.cargarPlatos();
  }

  cargarPlatos(): void {
    this.loading = true;
    this.error = '';
    
    this.platoService.listarActivosOrdenados().subscribe({
      next: (data) => {
        this.platos = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.message;
        this.loading = false;
        console.error('Error al cargar platos:', err);
      }
    });
  }

  confirmarEliminar(plato: PlatoResponse): void {
    const confirmar = confirm(`¿Estás seguro de eliminar el plato "${plato.nombre}"?`);
    
    if (confirmar) {
      this.eliminarPlato(plato.idPlato);
    }
  }

  eliminarPlato(id: number): void {
    this.platoService.eliminar(id).subscribe({
      next: () => {
        alert('Plato eliminado exitosamente');
        this.cargarPlatos();
      },
      error: (err) => {
        alert(`Error al eliminar: ${err.message}`);
        console.error('Error al eliminar plato:', err);
      }
    });
  }

  formatearPrecio(precio: number): string {
    return `S/. ${precio.toFixed(2)}`;
  }

  getBadgeClass(tipoPlato: string): string {
    const classes: { [key: string]: string } = {
      'E': 'bg-success',
      'S': 'bg-primary',
      'P': 'bg-warning',
      'B': 'bg-info'
    };
    return classes[tipoPlato] || 'bg-secondary';
  }
}