import { Component, inject, OnInit } from '@angular/core';
import { DetallePedidoServiceService } from '../../service/detalle-pedido-service.service';
import { Router } from '@angular/router';
import { AuthService } from '../../../auth/service/auth.service';
import { Pedido } from '../../../shared/model/pedido.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-lista-pedidos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './lista-pedidos.component.html',
  styleUrl: './lista-pedidos.component.css',
})
export class ListaPedidosComponent implements OnInit {
  private detallePedidoService = inject(DetallePedidoServiceService);
  private router = inject(Router);
  private authService = inject(AuthService);

  //Datos
  pedidos: Pedido[] = [];
  idUsuario: number | null = null;

  //Estados

  loading: boolean = false;
  error: string | null = null;

  ngOnInit(): void {
    this.verificarSesionYCargarPedidos();
  }
  verificarSesionYCargarPedidos(): void {
    const token = this.authService.getToken();

    if (!token) {
      console.error('❌ No hay token, redirigiendo al login');
      this.router.navigate(['/auth/login'], {
        queryParams: { returnUrl: '/cliente/pedido' },
      });
      return;
    }

    this.authService.getUsuario().subscribe({
      next: (response) => {
        this.idUsuario = response.idUsuario;
        console.log('ID Usuario:', this.idUsuario);
        this.cargarPedidos();
      },
      error: (error) => {
        console.error('Error al obtener usuario:', error);
        this.router.navigate(['/auth/login'], {
          queryParams: { returnUrl: '/cliente/pedido' },
        });
      },
    });
  }
  cargarPedidos(): void {
    if (!this.idUsuario) {
      console.error('No hay ID de usuario');
      return;
    }

    this.loading = true;
    this.detallePedidoService
      .obtenerPedidosDelCliente(this.idUsuario)
      .subscribe({
        next: (response) => {
          if (response.valor) {
            this.pedidos = response.data;
            console.log('Pedidos cargados:', this.pedidos.length);
          } else {
            this.error =
              response.mensaje || 'No se pudieron cargar los pedidos';
          }
          this.loading = false;
        },
        error: (err) => {
          console.error('❌ Error al cargar pedidos:', err);
          this.error = 'Error al cargar los pedidos';
          this.loading = false;
        },
      });
  }

  verDetalle(idPedido: number): void {
    console.log('Navegando al detalle del pedido:', idPedido);
    this.router.navigate(['/cliente/pedido', idPedido]);
  }

  getEstadoBadgeClass(estado: string): string {
    switch (estado) {
      case 'EP':
        return 'estado-en-proceso';
      case 'PA':
        return 'estado-pagado';
      case 'AN':
        return 'estado-anulado';
      default:
        return '';
    }
  }

  getEstadoTexto(estado: string): string {
    switch (estado) {
      case 'EP':
        return 'En Proceso';
      case 'PA':
        return 'Pagado';
      case 'AN':
        return 'Anulado';
      default:
        return estado;
    }
  }
}
