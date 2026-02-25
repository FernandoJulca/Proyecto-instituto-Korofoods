import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { DetallePedidoServiceService } from '../../../cliente/service/detalle-pedido-service.service';
import { WebsocketPedidosService } from '../../../cliente/pedido/websocket-pedidos.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../auth/service/auth.service';
import { DetallePedidoUsuarioResponse } from '../../../shared/response/detallePedidoUsuarioResponse';
import { Subscription } from 'rxjs';
import { DetallePedidoResponse } from '../../../shared/response/detallePedidoResponse';
import { PedidoWebSocketMessage } from '../../../cliente/pedido/detalle-pedido/detalle-pedido.component';
import iziToast from 'izitoast';
import { AlertIziToast } from '../../../util/iziToastAlert.service';
import { CommonModule } from '@angular/common';
import { AlertService } from '../../../util/alert.service';
import { MenuClienteService } from '../../../cliente/service/menuClienteService';
import { PlatoDto } from '../../../shared/dto/PlatoDto';
import { FormsModule } from '@angular/forms';
import { PagoService } from '../../../cliente/service/pago.service';
import { CrearPagoRequest } from '../../../cliente/pago/pagoDto';
import { DetallePedidoPagar } from '../../../shared/response/detallePedidoPagar.model';
import { PedidoMeseroService } from '../../service/pedidoMeseroService';

@Component({
  selector: 'app-detalle-ordenes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './detalle-ordenes.component.html',
  styleUrl: './detalle-ordenes.component.css',
  providers: [WebsocketPedidosService],
})
export class DetalleOrdenesComponent implements OnInit, OnDestroy {
  private detallePedidoService = inject(DetallePedidoServiceService);
  private pedidoService = inject(PedidoMeseroService);
  private wsPedidoService = inject(WebsocketPedidosService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private authService = inject(AuthService);
  private menuService = inject(MenuClienteService);
  private pagoService = inject(PagoService);

  idPedido: number | null = null;
  token: string | null = null;
  idUsuario: number | null = null;

  detalles: DetallePedidoResponse[] = [];
  cliente: DetallePedidoUsuarioResponse | null = null;
  pagoResponse: DetallePedidoPagar | null = null;

  plato: PlatoDto | null = null;
  platos: PlatoDto[] = [];
  platosFiltrados: PlatoDto[] = [];

  // Estados
  loadingPlatos: boolean = false;
  loading: boolean = false;
  wsConnected: boolean = false;
  error: string | null = null;

  mostrarModalPago = false;
  datosPago: any = null;
  metodoPagoCliente: string | null = null;
  pagoConfirmado = false;

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

  private subscriptions: Subscription = new Subscription();

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.idPedido = +params['id'];

      if (!this.idPedido || isNaN(this.idPedido)) {
        console.error('ID de pedido inválido');
        this.router.navigate(['/mesero/pedido']);
        return;
      }
      this.verificarSesion();
      this.cargarPlatos();
    });
  }

  verificarSesion(): void {
    this.token = this.authService.getToken();

    if (!this.token) {
      this.router.navigate(['/auth/login'], {
        queryParams: { returnUrl: `/mesero/pedido/${this.idPedido}` },
      });
      return;
    }

    this.authService.getUsuario().subscribe({
      next: (response) => {
        this.idUsuario = response.idUsuario;
        this.initializeWebSocket(this.idUsuario!, this.token!);
      },
      error: (error) => {
        console.error('Error al obtener usuario:', error);
        this.router.navigate(['/auth/login'], {
          queryParams: { returnUrl: `/mesero/pedido/${this.idPedido}` },
        });
      },
    });
  }

  private initializeWebSocket(userId: number, token: string): void {
    console.log('🔌 Conectando WebSocket Pedidos para mesero:', userId);
    this.wsPedidoService.connect(userId, token);

    this.subscriptions.add(
      this.wsPedidoService.isConnected().subscribe((connected) => {
        this.wsConnected = connected;
        if (connected && this.idPedido) {
          this.wsPedidoService.subscribeToPedidoMesero(this.idPedido);
          this.cargarDetallesPedido();
          this.cargarCliente();
        }
      }),
    );
    this.subscriptions.add(
      this.wsPedidoService.onPedidoMesero().subscribe((message) => {
        if (message) {
          this.actualizarVistaMesero(message);
        }
      }),
    );
    this.subscriptions.add(
      this.wsPedidoService.onPedidoError().subscribe((error) => {
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
    this.wsPedidoService.unsubscribeFromPedido();
  }
  cargarDetallesPedido(): void {
    if (!this.idPedido) {
      console.error('ERROR NO SE OBTUVO EL ID: ', this.idPedido);
      return;
    }

    this.loading = true;
    this.detallePedidoService.obtenerDetallePorPedido(this.idPedido).subscribe({
      next: (data) => {
        if (data.valor) {
          this.detalles = data.data;
          console.log(
            'Se obuvo la lista: ' + this.detalles.length + this.detalles,
          );
        }
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar detalles:', error);
        this.error = 'Error al cargar los detalles del pedido';
        this.loading = false;
      },
    });
  }
  cargarCliente(): void {
    if (!this.idPedido) return;

    this.detallePedidoService.obtenerCliente(this.idPedido).subscribe({
      next: (response) => {
        if (response.valor) {
          this.cliente = response.data;
          console.log('Cliente cargado:', this.cliente);
        }
      },
      error: (err) => {
        console.error('Error al cargar cliente:', err);
        this.error = 'Error al cargar información del cliente';
      },
    });
  }

  obtenerDetallePorId(idDetalle: number): DetallePedidoResponse | undefined {
    return this.detalles.find((dt) => dt.idDetalle === idDetalle);
  }

  entregarPlato(idDetalle: number): void {
    if (!this.idPedido) return;

    if (!this.wsConnected) {
      this.error = 'WebSocket no conectado. Intenta recargar la página.';
      setTimeout(() => (this.error = null), 3000);
      return;
    }

    var dtObtenido = this.obtenerDetallePorId(idDetalle);

    if (dtObtenido) {
      AlertService.confirm(
        `Entregar este pedido ${dtObtenido.nombre.split(' ')[0]}`,
      ).then((result: any) => {
        if (result.isConfirmed || result === true) {
          this.wsPedidoService.entregarPlato(this.idPedido!, idDetalle);
          AlertIziToast.success(
            'Exito!',
            `Entregaste la orden ${dtObtenido?.nombre.split(' ')[0]}`,
          );
        } else {
          AlertIziToast.info(
            'Info!',
            `no entregaste la orden ${dtObtenido?.nombre.split(' ')[0]}`,
          );
        }
      });
    }
  }

  private actualizarVistaMesero(message: any): void {
    if (message.detalles) {
      this.detalles = message.detalles;
    }

    if (message.infoCliente) {
      this.cliente = message.infoCliente;
    }

    if (message.inicioPago) {
      this.datosPago = message.pago;
      this.mostrarModalPago = true;
    }
    if (message.metodoPago) {
      this.metodoPagoCliente = message.metodoPago;
    }
    if (message.pagoConfirmado) {
      this.mostrarModalPago = false;
      this.pagoConfirmado = true;

      var metodoPago = message.metodoPago;
      const primerDetalle = this.detalles[0];

      const pagoRequest: CrearPagoRequest = {
        idPedido: this.idPedido!,
        idUsuario: this.idUsuario!,
        tipoPago: 'PP',
        monto: this.datosPago.totalPagar,
        metodoPago: this.metodoPagoCliente!,
        observaciones: `Depósito - ${metodoPago}`,
      };

      console.log('Creando pago:', pagoRequest);

      this.pagoService.crearPago(pagoRequest).subscribe({
        next: (pago) => {
          AlertService.success('Pago completado exitosamente!');
          this.detallePedidoService.cambiarEstado(this.idPedido!).subscribe({
            next: (response) => {
              console.log('Cambiadno de estado');
              this.pagoConfirmado = true;
              setTimeout(() => {
                this.router.navigate(['/mesero/ordenes']);
              }, 2000);
            },
            error: (error) => {
              console.error('Sucedio un problema con el pago');
            },
          });
        },
        error: (err) => {
          console.error('rror al crear pago:', err);
          this.error = 'Error al procesar el pago';
          setTimeout(() => (this.error = null), 5000);
        },
      });
    }
  }

  volver(): void {
    this.router.navigate(['/mesero/ordenes']);
  }

  procederAlPago(): void {
    const hayPendientes = this.detalles.some((x) => x.estado === 'PED');

    if (hayPendientes) {
      AlertService.info(
        'Tienes q entregar todos los platos o cancelar para proceder al pago',
      );
      return;
    }

    this.wsPedidoService.iniciarPago(this.idPedido!);
  }

  confirmarPago(): void {
    if (!this.metodoPagoCliente) return;
    this.wsPedidoService.confirmarPago(this.idPedido!, this.metodoPagoCliente);
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

  getEstadoIcono(estado: string): string {
    const iconos: { [key: string]: string } = {
      PED: 'fas fa-clock',
      ENT: 'fas fa-check-circle',
      CAN: 'fas fa-times-circle',
    };
    return iconos[estado] || 'fas fa-question-circle';
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
        console.error('❌ Error al cargar platos:', err);
        this.loadingPlatos = false;
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
          this.wsPedidoService.agregarPlato(this.idPedido!, idPlato, cantidad);
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
  getTipoPlatoLabel(tipo: string): string {
    const tipoObj = this.tiposPlato.find((t) => t.value === tipo);
    return tipoObj ? tipoObj.label : tipo;
  }

  // Obtener platos por estado
  get platosPendientes(): DetallePedidoResponse[] {
    return this.detalles.filter((d) => d.estado === 'PED');
  }

  get platosEntregados(): DetallePedidoResponse[] {
    return this.detalles.filter((d) => d.estado === 'ENT');
  }

  get platosCancelados(): DetallePedidoResponse[] {
    return this.detalles.filter((d) => d.estado === 'CAN');
  }

  calcularTotal(): number {
    return this.detalles
      .filter((detalle) => detalle.estado === 'ENT')
      .reduce((sum, detalle) => sum + detalle.subTotal, 0);
  }
}
