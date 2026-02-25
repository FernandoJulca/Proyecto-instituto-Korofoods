import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ReservaDto } from '../../shared/dto/ReservaDto';
import { PlatoDto } from '../../shared/dto/PlatoDto';
import { ReservaMeseroService } from '../service/reservaMeseroService';
import { MenuMeseroService } from '../service/menuMeseroService';
import { PedidoMeseroService } from '../service/pedidoMeseroService';
import { Router } from '@angular/router';
import { DetallePedidoRequestDTO } from '../../shared/dto/DetallePedidoRequestDTO';
import { PedidoRequestoDto } from '../../shared/dto/PedidoRequestDto';
import { trigger, transition, style, animate } from '@angular/animations';
import { AlertService } from '../../util/alert.service';
import { AuthService } from '../../auth/service/auth.service';
interface PlatoSeleccionado {
  plato: PlatoDto;
  cantidad: number;
}
@Component({
  selector: 'app-form-orden',
  imports: [CommonModule, FormsModule],
  templateUrl: './form-orden.component.html',
  styleUrl: './form-orden.component.css',
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate(
          '300ms ease-out',
          style({ opacity: 1, transform: 'translateY(0)' }),
        ),
      ]),
    ]),
    trigger('slideIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.9)' }),
        animate('300ms ease-out', style({ opacity: 1, transform: 'scale(1)' })),
      ]),
    ]),
  ],
})
export class FormOrdenComponent implements OnInit {
  currentStep = 1;

  reservaSearchId: string | null = null;
  reservaEncontrada: ReservaDto | null = null;
  searching = false;
  searchError: string | null = null;

  platos: PlatoDto[] = [];
  platosFiltrados: PlatoDto[] = [];
  platosSeleccionados: PlatoSeleccionado[] = [];
  loadingPlatos = false;
  errorPlatos: string | null = null;
  filtroPlato = '';
  filtroTipo = '';
  tiposPlato: string[] = [];

  // Propiedades de paginación
  paginaActual = 1;
  platosPorPagina = 12;
  totalPaginas = 0;
  idMesero!: number;
  creandoOrden = false;
  ordenCreada: any = null;

  constructor(
    private reservaService: ReservaMeseroService,
    private menuService: MenuMeseroService,
    private pedidoService: PedidoMeseroService,
    private router: Router,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.authService.getUsuario().subscribe(
      (user) => {
        this.idMesero = user.idUsuario;
        console.log('ID del mesero logueado:', this.idMesero);
      },
      (err) => console.error(err),
    );
  }

  buscarReserva(): void {
    if (!this.reservaSearchId) {
      this.searchError = 'Por favor ingrese un ID de reserva';
      return;
    }

    this.searching = true;
    this.searchError = null;
    this.reservaEncontrada = null;

    this.reservaService.getReservationById(this.reservaSearchId).subscribe({
      next: (response) => {
        if (response.valor && response.data) {
          this.reservaEncontrada = response.data;
        } else {
          this.searchError = response.mensaje || 'No se encontró la reserva';
        }
        this.searching = false;
      },
      error: (err) => {
        this.searching = false;
        const mensajeError =
          err?.error?.mensaje || err?.message || 'Error al crear la orden';
        this.searchError = mensajeError;
      },
    });
  }

  continuarAPlatos(): void {
    this.currentStep = 2;
    this.cargarPlatos();
  }

  cargarPlatos(): void {
    this.loadingPlatos = true;
    this.errorPlatos = null;

    this.menuService.listarPlatos().subscribe({
      next: (response) => {
        if (response.valor && response.data) {
          this.platos = response.data;
          this.platosFiltrados = [...this.platos];
          this.extraerTiposPlato();
          this.calcularTotalPaginas();
        } else {
          this.errorPlatos = response.mensaje || 'Error al cargar platos';
        }
        this.loadingPlatos = false;
      },
      error: (err) => {
        this.errorPlatos = 'Error al cargar el menú';
        this.loadingPlatos = false;
        console.error('Error:', err);
      },
    });
  }

  extraerTiposPlato(): void {
    const tipos = new Set(this.platos.map((p) => p.tipoPlato));
    this.tiposPlato = Array.from(tipos);
  }

  platosFiltradosMetodo(): PlatoDto[] {
    let filtrados = [...this.platos];

    if (this.filtroPlato) {
      const busqueda = this.filtroPlato.toLowerCase();
      filtrados = filtrados.filter((p) =>
        p.nombre.toLowerCase().includes(busqueda),
      );
    }

    if (this.filtroTipo) {
      filtrados = filtrados.filter((p) => p.tipoPlato === this.filtroTipo);
    }

    // Recalcular paginación cuando cambian los filtros
    this.calcularTotalPaginas(filtrados.length);

    // Aplicar paginación
    const inicio = (this.paginaActual - 1) * this.platosPorPagina;
    const fin = inicio + this.platosPorPagina;

    return filtrados.slice(inicio, fin);
  }

  calcularTotalPaginas(totalItems?: number): void {
    const total = totalItems ?? this.platos.length;
    this.totalPaginas = Math.ceil(total / this.platosPorPagina);

    // Ajustar página actual si está fuera de rango
    if (this.paginaActual > this.totalPaginas) {
      this.paginaActual = Math.max(1, this.totalPaginas);
    }
  }

  cambiarPagina(pagina: number): void {
    if (pagina >= 1 && pagina <= this.totalPaginas) {
      this.paginaActual = pagina;
      // Scroll suave hacia arriba del grid
      document
        .querySelector('.platos-grid')
        ?.scrollIntoView({ behavior: 'smooth' });
    }
  }

  get paginasArray(): number[] {
    return Array.from({ length: this.totalPaginas }, (_, i) => i + 1);
  }

  // Método para obtener rango de páginas visible (evita mostrar 100 botones)
  get paginasVisibles(): number[] {
    const maxPaginas = 5; // Mostrar máximo 5 números de página
    const mitad = Math.floor(maxPaginas / 2);

    let inicio = Math.max(1, this.paginaActual - mitad);
    let fin = Math.min(this.totalPaginas, inicio + maxPaginas - 1);

    // Ajustar inicio si estamos cerca del final
    if (fin - inicio < maxPaginas - 1) {
      inicio = Math.max(1, fin - maxPaginas + 1);
    }

    return Array.from({ length: fin - inicio + 1 }, (_, i) => inicio + i);
  }

  // Llamar esto cuando cambien los filtros
  aplicarFiltros(): void {
    this.paginaActual = 1; // Resetear a página 1 al filtrar
    this.platosFiltradosMetodo();
  }

  agregarPlato(plato: PlatoDto): void {
    this.platosSeleccionados.push({
      plato: plato,
      cantidad: 1,
    });
  }

  removerPlato(idPlato: number): void {
    this.platosSeleccionados = this.platosSeleccionados.filter(
      (item) => item.plato.idPlato !== idPlato,
    );
  }

  isPlatoSeleccionado(idPlato: number): boolean {
    return this.platosSeleccionados.some(
      (item) => item.plato.idPlato === idPlato,
    );
  }

  getCantidad(idPlato: number): number {
    const item = this.platosSeleccionados.find(
      (item) => item.plato.idPlato === idPlato,
    );
    return item ? item.cantidad : 0;
  }

  incrementarCantidad(idPlato: number): void {
    const item = this.platosSeleccionados.find(
      (item) => item.plato.idPlato === idPlato,
    );
    if (item) {
      item.cantidad++;
    }
  }

  decrementarCantidad(idPlato: number): void {
    const item = this.platosSeleccionados.find(
      (item) => item.plato.idPlato === idPlato,
    );
    if (item) {
      if (item.cantidad > 1) {
        item.cantidad--;
      } else {
        this.removerPlato(idPlato);
      }
    }
  }

  actualizarCantidad(idPlato: number, event: any): void {
    const cantidad = parseInt(event.target.value);
    if (cantidad > 0) {
      const item = this.platosSeleccionados.find(
        (item) => item.plato.idPlato === idPlato,
      );
      if (item) {
        item.cantidad = cantidad;
      }
    }
  }

  calcularSubtotal(): number {
    return this.platosSeleccionados.reduce(
      (sum, item) => sum + item.plato.precio * item.cantidad,
      0,
    );
  }

  calcularTotal(): number {
    // Por ahora es igual al subtotal, pero puedes agregar impuestos, descuentos, etc.
    return this.calcularSubtotal();
  }

  volverABusqueda(): void {
    this.currentStep = 1;
    this.platosSeleccionados = [];
  }

  // ============ CREAR ORDEN ============

  crearOrden(): void {
    if (!this.reservaEncontrada || this.platosSeleccionados.length === 0) {
      return;
    }

    this.creandoOrden = true;

    const detalles: DetallePedidoRequestDTO[] = this.platosSeleccionados.map(
      (item) => ({
        idPlato: item.plato.idPlato,
        cantidad: item.cantidad,
      }),
    );

    const pedidoRequest: PedidoRequestoDto = {
      idMesa: this.reservaEncontrada.mesa,
      idUsuario: this.idMesero, // luego se va cambiar por el mesero
      idReserva: this.reservaEncontrada.idReserva,
      detalles: detalles,
    };

    this.pedidoService.crearPedido(pedidoRequest).subscribe({
      next: (response) => {
        if (response.valor && response.data) {
          this.ordenCreada = response.data;
          AlertService.success(
            response.mensaje || 'Pedido registrado correctamente',
          );
          this.irAOrdenes();
        } else {
          AlertService.error(response.mensaje || 'Error al crear la orden.');
          console.log(response.mensaje);
        }
        this.creandoOrden = false;
      },
      error: (err) => {
        this.creandoOrden = false;
        const mensajeError =
          err?.error?.mensaje || err?.message || 'Error al crear la orden';
        AlertService.error(mensajeError);
        console.log(err);
      },
    });
  }

  irAOrdenes(): void {
    this.router.navigate(['/mesero/ordenes']);
  }

  obtenerNombreTipo(tipo: string): string {
    const tipos: { [key: string]: string } = {
      E: 'Entrada',
      S: 'Segundo',
      P: 'Postre',
      B: 'Bebida',
    };
    return tipos[tipo] || tipo;
  }

  onImgError(event: any) {
    event.target.src = '/img/no-imagen.jpg';
  }
}
