// crudEventos/evento-list/evento-list.component.ts

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { EventoService } from '../../service/evento.service';
import { EventoResponse } from '../../models/evento.model';

@Component({
  selector: 'app-evento-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './evento.list.component.html',
  styleUrl: './evento.list.component.css'
})
export class EventoListComponent implements OnInit {
  eventos: EventoResponse[] = [];
  loading: boolean = false;
  error: string = '';

  constructor(private eventoService: EventoService) {}

  ngOnInit(): void {
    this.cargarEventos();
  }

  cargarEventos(): void {
    this.loading = true;
    this.error = '';
    
    this.eventoService.listarTodos().subscribe({
      next: (data) => {
        this.eventos = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.message;
        this.loading = false;
        console.error('Error al cargar eventos:', err);
      }
    });
  }

  confirmarEliminar(evento: EventoResponse): void {
    const confirmar = confirm(`¿Estás seguro de eliminar el evento "${evento.nombre}"?`);
    
    if (confirmar) {
      this.eliminarEvento(evento.idEvento);
    }
  }

  eliminarEvento(id: number): void {
    this.eventoService.eliminar(id).subscribe({
      next: () => {
        alert('Evento eliminado exitosamente');
        this.cargarEventos(); // Recargar la lista
      },
      error: (err) => {
        alert(`Error al eliminar: ${err.message}`);
        console.error('Error al eliminar evento:', err);
      }
    });
  }

  formatearFecha(fecha: string): string {
    const date = new Date(fecha);
    return date.toLocaleString('es-ES', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  formatearCosto(costo: number): string {
    return `S/. ${costo.toFixed(2)}`;
  }

  
  onImageError(event: any): void {
  event.target.style.display = 'none';
  event.target.parentElement.innerHTML = `
    <div class="evento-sin-imagen">
      <i class="bi bi-image"></i>
    </div>
  `;
}
}