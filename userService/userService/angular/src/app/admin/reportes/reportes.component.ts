import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReservaServiceService } from '../../cliente/service/reserva-service.service';
import { VentasPorFechaMesaDto } from '../../shared/dto/VentasPorFechaMesaDto';
import { PlatoMasVendidoDto } from '../../shared/dto/PlatoMasVendidoDto';
import { PedidoMeseroService } from '../../mesero/service/pedidoMeseroService';
import { FormsModule } from '@angular/forms';
import { PagoService } from '../../cliente/service/pago.service';
import { PlatoService } from '../service/plato.service';
import { EventoService } from '../service/evento.service';

interface ReporteItem {
  id: string;
  titulo: string;
  descripcion: string;
  icono: string;
  color: string;
  servicio: 'reserva' | 'menu' | 'evento' | 'usuario' | 'pago';
}

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reportes.component.html',
  styleUrl: './reportes.component.css',
})
export class ReportesComponent {
  loading: { [key: string]: boolean } = {};
  error: string = '';

  // HU22 - Ventas por fecha y mesa
  filtroVentas = {
    fechaInicio: this.formatearFecha(new Date(new Date().setDate(1))), // primer día del mes
    fechaFin: this.formatearFecha(new Date()),
    idMesa: null as number | null,
  };
  tablaVentas: VentasPorFechaMesaDto[] = [];
  mostrarTablaVentas = false;

  // HU23 - Platos más vendidos
  filtroPlatos = {
    fechaInicio: this.formatearFecha(new Date(new Date().setDate(1))),
    fechaFin: this.formatearFecha(new Date()),
  };
  tablaPlatos: PlatoMasVendidoDto[] = [];
  mostrarTablaPlatos = false;

  reportes: ReporteItem[] = [
    {
      id: 'reservas',
      titulo: 'Reporte de Reservas',
      descripcion: 'Todas las reservas registradas en el sistema',
      icono: 'bi-calendar-check-fill',
      color: 'primary',
      servicio: 'reserva',
    },
    {
      id: 'ingresos',
      titulo: 'Reporte de Ingresos',
      descripcion: 'Consolidado de ingresos por pagos y transacciones',
      icono: 'bi-cash-stack',
      color: 'success',
      servicio: 'pago',
    },
    {
      id: 'platos',
      titulo: 'Reporte de Platos',
      descripcion: 'Listado completo del menú y stock',
      icono: 'bi-egg-fried',
      color: 'warning',
      servicio: 'menu',
    },
    {
      id: 'eventos',
      titulo: 'Reporte de Eventos',
      descripcion: 'Eventos realizados y programados',
      icono: 'bi-calendar-event-fill',
      color: 'danger',
      servicio: 'evento',
    },
  ];

  constructor(
    private reservaService: ReservaServiceService,
    private pedidoService: PedidoMeseroService,
    private pagoService: PagoService,
    private menuService: PlatoService,
    private eventoService: EventoService,
  ) {}

  descargarReporte(reporte: ReporteItem): void {
    this.loading[reporte.id] = true;
    this.error = '';

    // Llamar al servicio correspondiente
    switch (reporte.id) {
      case 'reservas':
        this.descargarReporteReservas();
        break;
      case 'ingresos':
        this.descargarReporteIngresos();
        break;
      case 'platos':
        this.descargarReportePlatos();
        break;
      case 'eventos':
        this.descargarReporteEventos();
        break;
      default:
        this.loading[reporte.id] = false;
    }
  }

  // HU22 - Consultar ventas por fecha y mesa
  consultarVentasPorMesa(): void {
    this.loading['ventas-mesa'] = true;
    this.error = '';
    this.mostrarTablaVentas = false;

    this.pedidoService
      .reporteVentasPorMesa(
        this.filtroVentas.fechaInicio,
        this.filtroVentas.fechaFin,
        this.filtroVentas.idMesa ?? undefined,
      )
      .subscribe({
        next: (resp) => {
          this.tablaVentas = resp.data ?? [];
          this.mostrarTablaVentas = true;
          this.loading['ventas-mesa'] = false;
        },
        error: (err) => {
          this.error = 'Error al obtener reporte de ventas por mesa';
          this.loading['ventas-mesa'] = false;
          console.error(err);
        },
      });
  }

  // HU23 - Consultar platos más vendidos
  consultarPlatosMasVendidos(): void {
    this.loading['platos-vendidos'] = true;
    this.error = '';
    this.mostrarTablaPlatos = false;

    this.pedidoService
      .reportePlatosMasVendidos(
        this.filtroPlatos.fechaInicio,
        this.filtroPlatos.fechaFin,
      )
      .subscribe({
        next: (resp) => {
          this.tablaPlatos = resp.data ?? [];
          this.mostrarTablaPlatos = true;
          this.loading['platos-vendidos'] = false;
        },
        error: (err) => {
          this.error = 'Error al obtener reporte de platos más vendidos';
          this.loading['platos-vendidos'] = false;
          console.error(err);
        },
      });
  }

  getTipoPlato(tipo: string): string {
    const tipos: { [key: string]: string } = {
      E: 'Entrada',
      S: 'Sopa',
      P: 'Plato Principal',
      B: 'Bebida',
    };
    return tipos[tipo] ?? tipo;
  }

  getTotalVentas(): number {
    return this.tablaVentas.reduce((sum, v) => sum + v.totalVentas, 0);
  }

  getTotalPlatos(): number {
    return this.tablaPlatos.reduce((sum, p) => sum + p.cantidadVendida, 0);
  }

  private descargarReporteReservas(): void {
    // Generar reporte de todas las reservas (últimos 6 meses)
    const fechaFin = new Date();
    const fechaInicio = new Date();
    fechaInicio.setMonth(fechaInicio.getMonth() - 6);

    const request = {
      fechaInicio: this.formatearFecha(fechaInicio),
      fechaFin: this.formatearFecha(fechaFin),
    };

    this.reservaService.generarReporteReservas(request).subscribe({
      next: (blob) => {
        this.descargarPDF(blob, 'reporte-reservas.pdf');
        this.loading['reservas'] = false;
      },
      error: (err) => {
        this.error = 'Error al generar el reporte de reservas';
        this.loading['reservas'] = false;
        console.error('❌ Error:', err);
      },
    });
  }

  private descargarReporteIngresos(): void {
    this.pagoService.generarReporteIngresos().subscribe({
      next: (blob) => {
        this.descargarPDF(blob, 'reporte-ingresos.pdf');
        this.loading['ingresos'] = false;
      },
      error: (err) => {
        this.error = 'Error al generar el reporte de ingresos';
        this.loading['ingresos'] = false;
        console.error(err);
      },
    });
  }

  private descargarReportePlatos(): void {
  this.menuService.generarReportePlatos().subscribe({
    next: (blob) => {
      this.descargarPDF(blob, 'reporte-platos.pdf');
      this.loading['platos'] = false;
    },
    error: () => {
      this.error = 'Error al generar el reporte de platos';
      this.loading['platos'] = false;
    }
  });
}

private descargarReporteEventos(): void {
  this.eventoService.generarReporteEventos().subscribe({
    next: (blob) => {
      this.descargarPDF(blob, 'reporte-eventos.pdf');
      this.loading['eventos'] = false;
    },
    error: () => {
      this.error = 'Error al generar el reporte de eventos';
      this.loading['eventos'] = false;
    }
  });
}

  private mostrarProximamente(titulo: string): void {
    setTimeout(() => {
      alert(`${titulo} - Próximamente disponible`);
      this.loading = {};
    }, 500);
  }

  private descargarPDF(blob: Blob, nombreArchivo: string): void {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = nombreArchivo;
    link.click();
    window.URL.revokeObjectURL(url);
  }

  private formatearFecha(fecha: Date): string {
    const year = fecha.getFullYear();
    const month = String(fecha.getMonth() + 1).padStart(2, '0');
    const day = String(fecha.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  getLoadingState(id: string): boolean {
    return this.loading[id] || false;
  }

  getTotalGeneradoPlatos(): number {
    return this.tablaPlatos.reduce((sum, p) => sum + p.totalGenerado, 0);
  }
}
