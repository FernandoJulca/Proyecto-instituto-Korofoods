import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { DetallePedidoServiceService } from '../../service/detalle-pedido-service.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../auth/service/auth.service';
import { MenuClienteService } from '../../service/menuClienteService';
import { DetallePedidoResponse } from '../../../shared/response/detallePedidoResponse';
import { PlatoDto } from '../../../shared/dto/PlatoDto';
import { DetallePedidoMeseroResponse } from '../../../shared/response/detallePedidoMeseroResponse';
import { Subscription } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { WebsocketPedidosService } from '../websocket-pedidos.service';
import { AlertService } from '../../../util/alert.service';
import { AlertIziToast } from '../../../util/iziToastAlert.service';
import { EstadoPedido } from '../../../shared/enums/estadoPedido.enum';

export interface PedidoWebSocketMessage {
  detalles: any[];
  infoMesero?: any;
  infoCliente?: any;
}
@Component({
  selector: 'app-detalle-pedido',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './detalle-pedido.component.html',
  styleUrl: './detalle-pedido.component.css',
  providers: [WebsocketPedidosService],
})
export class DetallePedidoComponent implements OnInit, OnDestroy {
  private detallePedidoService = inject(DetallePedidoServiceService);
  private wsService = inject(WebsocketPedidosService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private authService = inject(AuthService);
  private menuService = inject(MenuClienteService);

  // ID del pedido desde la ruta
  idPedido: number | null = null;

  // Usuario
  idUsuario: number | null = null;
  token: string | null = null;

  mostrarModalMetodo = false;
  metodoSeleccionado: string | null = null;
  pagoConfirmado = false;

  imagenPreview: string | null = null;
  imagenBase64: string | null = null;
  errorPago: string = '';

  // QRs estáticos
  qrYapeUrl: string = 'assets/yape-qr.jpeg';
  qrPlinUrl: string = 'assets/plin-qr.jpeg';

  // Plin - Número ficticio
  numeroPlin: string = '986425458';
  datosPago: any = null;
  // Datos
  detalles: DetallePedidoResponse[] = [];
  platos: PlatoDto[] = [];
  platosFiltrados: PlatoDto[] = [];
  mesero: DetallePedidoMeseroResponse | null = null;
  plato: PlatoDto | null = null;

  // Cantidades
  cantidadesPlatos: Map<number, number> = new Map();

  // Filtros
  filtroNombre: string = '';
  filtroTipo: string = 'TODOS';
  tiposPlato = [
    { value: 'TODOS', label: 'Todos' },
    { value: 'E', label: 'Entradas' },
    { value: 'S', label: 'Segundos' },
    { value: 'P', label: 'Postres' },
    { value: 'B', label: 'Bebidas' },
  ];

  // Estados
  loading: boolean = false;
  loadingPlatos: boolean = false;
  wsConnected: boolean = false;
  error: string | null = null;
  estadoPedido: EstadoPedido | null = null;

  private subscriptions: Subscription = new Subscription();

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.idPedido = +params['id'];
      console.log('ID Pedido desde ruta:', this.idPedido);

      if (!this.idPedido || isNaN(this.idPedido)) {
        console.error('ID de pedido inválido');
        this.router.navigate(['/cliente/pedido']);
        return;
      }
      this.verificarSesion();
    });
    this.cargarPlatos();
  }

  verificarSesion(): void {
    this.token = this.authService.getToken();

    if (!this.token) {
      console.error('No hay token');
      this.router.navigate(['/auth/login'], {
        queryParams: { returnUrl: `/cliente/pedido/${this.idPedido}` },
      });
      return;
    }

    this.authService.getUsuario().subscribe({
      next: (response) => {
        this.idUsuario = response.idUsuario;
        console.log('ID Usuario:', this.idUsuario);

        this.initializeWebSocket(this.idUsuario!, this.token!);
      },
      error: (error) => {
        console.error('Error al obtener usuario:', error);
        this.router.navigate(['/auth/login'], {
          queryParams: { returnUrl: `/cliente/pedido/${this.idPedido}` },
        });
      },
    });
  }
  private initializeWebSocket(userId: number, token: string): void {
    console.log('Conectando WebSocket para pedido:', this.idPedido);
    this.wsService.connect(userId, token);

    this.subscriptions.add(
      this.wsService.isConnected().subscribe((connected) => {
        this.wsConnected = connected;
        if (connected && this.idPedido) {
          console.log('WebSocket conectado');

          this.wsService.subscribeToPedidoCliente(this.idPedido);

          this.cargarDetallesPedido();
          this.cargarMesero();
          this.cargarEstadoPedido();
        }
      }),
    );

    this.subscriptions.add(
      this.wsService.onPedidoCliente().subscribe((message) => {
        if (message) {
          console.log('Actualización recibida:', message);
          this.actualizarVistaCliente(message);
        }
      }),
    );

    this.subscriptions.add(
      this.wsService.onPedidoError().subscribe((error) => {
        if (error) {
          console.error('Error:', error);
          this.error = error.mensaje || 'Error desconocido';
          setTimeout(() => (this.error = null), 5000);
        }
      }),
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
    this.wsService.unsubscribeFromPedido();
  }

  elegirMetodo(metodo: string): void {
    this.metodoSeleccionado = metodo;
    this.wsService.elegirMetodo(this.idPedido!, metodo);
  }

  cerrarYVolver(): void {
    this.pagoConfirmado = false;
    this.router.navigate(['/cliente/pedido']);
  }

  cargarPlatos(): void {
    this.loadingPlatos = true;
    this.menuService.listarPlatos().subscribe({
      next: (response) => {
        if (response.valor) {
          this.platos = response.data;
          this.platosFiltrados = response.data;

          this.platos.forEach((plato) => {
            this.cantidadesPlatos.set(plato.idPlato, 1);
          });
        }
        this.loadingPlatos = false;
      },
      error: (err) => {
        console.error(' Error al cargar platos:', err);
        this.loadingPlatos = false;
      },
    });
  }

  cargarDetallesPedido(): void {
    if (!this.idPedido) return;

    this.loading = true;
    this.detallePedidoService.obtenerDetallePorPedido(this.idPedido).subscribe({
      next: (response) => {
        if (response.valor) {
          this.detalles = response.data;
          console.log('Detalles cargados:', this.detalles.length);
        }
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al cargar detalles:', err);
        this.loading = false;
      },
    });
  }

  cargarEstadoPedido(): void {
    if (!this.idPedido) return;
    this.detallePedidoService.obtenerPedido(this.idPedido).subscribe({
      next: (response) => {
        if (response.valor) {
          console.log('Estado:', response.data.estado);
          this.estadoPedido = response.data.estado;
        }
      },
      error: (err) => console.error('Error al cargar pedido:', err),
    });
  }
  cargarMesero(): void {
    if (!this.idPedido) return;

    this.detallePedidoService.obtnerMesero(this.idPedido).subscribe({
      next: (response) => {
        if (response.valor) {
          this.mesero = response.data;
          console.log(' Mesero cargado');
        }
      },
      error: (err) => {
        console.error('Error al cargar mesero:', err);
      },
    });
  }
  aplicarFiltros(): void {
    this.platosFiltrados = this.platos.filter((plato) => {
      const cumpleNombre = plato.nombre
        .toLowerCase()
        .includes(this.filtroNombre.toLowerCase());
      const cumpleTipo =
        this.filtroTipo === 'TODOS' || plato.tipoPlato === this.filtroTipo;

      return cumpleNombre && cumpleTipo;
    });
  }

  limpiarFiltros(): void {
    this.filtroNombre = '';
    this.filtroTipo = 'TODOS';
    this.platosFiltrados = this.platos;
  }

  incrementarCantidad(idPlato: number): void {
    const cantidad = this.cantidadesPlatos.get(idPlato) || 1;
    this.cantidadesPlatos.set(idPlato, cantidad + 1);
  }

  decrementarCantidad(idPlato: number): void {
    const cantidad = this.cantidadesPlatos.get(idPlato) || 1;
    if (cantidad > 1) {
      this.cantidadesPlatos.set(idPlato, cantidad - 1);
    }
  }

  getCantidad(idPlato: number): number {
    return this.cantidadesPlatos.get(idPlato) || 1;
  }

  obtenerPlato(idPlato: number): PlatoDto | undefined {
    return this.platos.find((plato) => plato.idPlato === idPlato);
  }
  obtenerPlatoDetalle(idDetalle: number): DetallePedidoResponse | undefined {
    return this.detalles.find((detalles) => detalles.idDetalle === idDetalle);
  }

  agregarPlato(idPlato: number): void {
    if (!this.idPedido) {
      this.error = 'No hay pedido seleccionado';
      setTimeout(() => (this.error = null), 3000);
      return;
    }

    var platoObtenido = this.obtenerPlato(idPlato);

    if (platoObtenido) {
      AlertService.confirm(
        `${platoObtenido?.nombre}`,
        '¿Quieres agregar el plato?',
      ).then((resultado: any) => {
        if (resultado.isConfirmed || resultado === true) {
          const cantidad = this.getCantidad(idPlato);
          this.wsService.agregarPlato(this.idPedido!, idPlato, cantidad);
          this.cantidadesPlatos.set(idPlato, 1);
          AlertIziToast.success(
            'Exito!',
            `Se agrego el plato ${platoObtenido!.nombre.split(' ')[0]}`,
          );
        } else {
          AlertIziToast.info(
            'No se agrego el plato, ',
            `${platoObtenido?.nombre.split(' ')[0]}`,
          );
        }
      });
    } else {
      AlertService.error('Error!', 'No se encontro el plato');
    }
  }

  cancelarPlato(idDetalle: number): void {
    if (!this.idPedido) {
      console.error('No hay pedido seleccionado');
      return;
    }

    var detalleObtenido = this.obtenerPlatoDetalle(idDetalle);
    if (detalleObtenido) {
      AlertService.confirm(
        'Despues de cancelar, el pedido no sera entregado',
        '¿Quieres cancelar este plato?',
      ).then((resultado: any) => {
        if (resultado.isConfirmed || resultado === true) {
          this.wsService.cancelarPlato(this.idPedido!, idDetalle);
          AlertIziToast.warning(
            'Exito!',
            `cancelaste la orden ${detalleObtenido?.nombre.split(' ')[0]}`,
          );
        } else {
          AlertIziToast.info(
            'Info!',
            `no se cancelo la orden ${detalleObtenido?.nombre.split(' ')[0]}`,
          );
        }
      });
    }
  }

  private actualizarVistaCliente(message: any): void {
    if (message.detalles) {
      this.detalles = message.detalles;
    }

    if (message.infoMesero) {
      this.mesero = message.infoMesero;
    }
    if (message.inicioPago) {
      this.datosPago = message.pago;
      this.mostrarModalMetodo = true;
    }

    if (message.pagoConfirmado) {
      this.mostrarModalMetodo = false;
      this.pagoConfirmado = true;
      AlertService.success(
        'Pagaste correctamente el pedido, gracias por tu visita!',
      );
      this.estadoPedido;
    }
  }

  getTipoPlatoLabel(tipo: string): string {
    const tipoObj = this.tiposPlato.find((t) => t.value === tipo);
    return tipoObj ? tipoObj.label : tipo;
  }

  getEstadoClase(estado: string): string {
    switch (estado) {
      case 'PED':
        return 'estado-pedido';
      case 'ENT':
        return 'estado-entregado';
      case 'CAN':
        return 'estado-cancelado';
      default:
        return '';
    }
  }

  getEstadoTexto(estado: string): string {
    switch (estado) {
      case 'PED':
        return 'Pedido';
      case 'ENT':
        return 'Entregado';
      case 'CAN':
        return 'Cancelado';
      default:
        return estado;
    }
  }

  calcularTotal(): number {
    return this.detalles
      .filter((detalle) => detalle.estado === 'ENT')
      .reduce((sum, detalle) => sum + detalle.subTotal, 0);
  }

  volver(): void {
    this.router.navigate(['/cliente/pedido']);
  }

  cerrarDialog(): void {
    const info = document.getElementById('info') as HTMLDialogElement;

    info.classList.add('saliendo');

    setTimeout(() => {
      info.close();
      info.classList.remove('saliendo');
    }, 250);
  }
}
