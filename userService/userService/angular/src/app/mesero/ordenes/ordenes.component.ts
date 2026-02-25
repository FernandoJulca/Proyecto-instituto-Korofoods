import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { interval, Subscription, forkJoin } from 'rxjs';
import { PedidoResumenDto } from '../../shared/dto/PedidoResumenDto';
import { CommonModule } from '@angular/common';
import { PedidoMeseroService } from '../service/pedidoMeseroService';
import { Router } from '@angular/router';

interface FilterTab {
  label: string;
  value: string;
}

@Component({
  selector: 'app-ordenes',
  imports: [CommonModule],
  templateUrl: './ordenes.component.html',
  styleUrl: './ordenes.component.css',
})
export class OrdenesComponent implements OnInit, OnDestroy {
  pedidos: PedidoResumenDto[] = [];
  pedidosFiltrados: PedidoResumenDto[] = [];
  loading = false;
  error: string | null = null;
  selectedFilter = 'todos';

  // Cache para contadores
  contadores: { [key: string]: number } = {
    todos: 0,
    EP: 0,
  };

  filterTabs: FilterTab[] = [
    { label: 'Todos', value: 'todos' },
    { label: 'En Proceso', value: 'EP' },
  ];

  private refreshSubscription?: Subscription;
  private router = inject(Router);
  constructor(private pedidoService: PedidoMeseroService) {}

  ngOnInit(): void {
    this.cargarPedidos();
    this.cargarContadores();

    // Auto-refresh cada 30 segundos
    this.refreshSubscription = interval(30000).subscribe(() => {
      this.cargarPedidos(true);
      this.cargarContadores();
    });
  }

  ngOnDestroy(): void {
    if (this.refreshSubscription) {
      this.refreshSubscription.unsubscribe();
    }
  }

  cargarContadores(): void {
    // Carga todos los contadores en paralelo
    forkJoin({
      todos: this.pedidoService.listarPedidos(),
      pendientes: this.pedidoService.listarPedidos('EP'),
    }).subscribe({
      next: (results) => {
        this.contadores['todos'] = results.todos.data?.length || 0;
        this.contadores['EP'] = results.pendientes.data?.length || 0;
      },
      error: (err) => {
        console.error('Error al cargar contadores:', err);
      },
    });
  }

  cargarPedidos(silencioso: boolean = false): void {
    if (!silencioso) {
      this.loading = true;
    }
    this.error = null;

    // Si hay un filtro activo diferente de 'todos', usar el filtro en el backend
    const estadoFiltro =
      this.selectedFilter !== 'todos' ? this.selectedFilter : undefined;

    this.pedidoService.listarPedidos(estadoFiltro).subscribe({
      next: (response) => {
        if (response.valor && response.data) {
          this.pedidos = response.data;
          this.pedidosFiltrados = [...this.pedidos];
        } else {
          this.error = response.mensaje || 'Error al cargar órdenes';
        }
        this.loading = false;
      },
      error: (err) => {
        this.error = 'No se pudo conectar con el servidor';
        this.loading = false;
        console.error('Error al cargar pedidos:', err);
      },
    });
  }

  filterByStatus(status: string): void {
    this.selectedFilter = status;
    this.cargarPedidos();
  }

  getCountByStatus(status: string): number {
    return this.contadores[status] || 0;
  }

  getEstadoClass(estado: string): string {
    const estadoMap: { [key: string]: string } = {
      EP: 'estado-pendiente',
    };
    return estadoMap[estado] || 'estado-default';
  }

  getEstadoLabel(estado: string): string {
    const estadoLabels: { [key: string]: string } = {
      EP: 'En Proceso',
    };
    return estadoLabels[estado] || estado;
  }

  formatFecha(fechaHora: string): string {
    const fecha = new Date(fechaHora);
    const hoy = new Date();

    if (fecha.toDateString() === hoy.toDateString()) {
      return 'Hoy';
    }

    const ayer = new Date(hoy);
    ayer.setDate(ayer.getDate() - 1);
    if (fecha.toDateString() === ayer.toDateString()) {
      return 'Ayer';
    }

    return fecha.toLocaleDateString('es-PE', {
      day: '2-digit',
      month: 'short',
    });
  }

  formatHora(fechaHora: string): string {
    const fecha = new Date(fechaHora);
    return fecha.toLocaleTimeString('es-PE', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: true,
    });
  }

  getTiempoTranscurrido(fechaHora: string): string {
    const fecha = new Date(fechaHora);
    const ahora = new Date();
    const diffMs = ahora.getTime() - fecha.getTime();
    const diffMins = Math.floor(diffMs / 60000);

    if (diffMins < 1) {
      return 'Hace unos segundos';
    } else if (diffMins < 60) {
      return `Hace ${diffMins} min`;
    } else {
      const diffHours = Math.floor(diffMins / 60);
      return `Hace ${diffHours} h`;
    }
  }

  verDetalle(idPedido: number): void {
    console.log('Ver detalle del pedido:', idPedido);
    this.router.navigate(['/mesero/ordenes', idPedido]);
  }
}
