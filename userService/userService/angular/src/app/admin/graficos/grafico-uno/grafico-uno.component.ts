import {
  AfterViewInit,
  Component,
  ElementRef,
  inject,
  OnDestroy,
  ViewChild,
} from '@angular/core';
import { filter, auditTime } from 'rxjs';

import { Chart, ChartConfiguration } from 'chart.js';
import { Subscription } from 'rxjs';
import { SocketGraficoService } from '../../service/socket-grafico.service';
import { GraficoUnoDto } from '../../../shared/dto/graficoUnoDto.model';

@Component({
  selector: 'app-grafico-uno',
  standalone: true,
  imports: [],
  templateUrl: './grafico-uno.component.html',
  styleUrl: './grafico-uno.component.css',
})
export class GraficoUnoComponent implements AfterViewInit, OnDestroy {
  @ViewChild('graficoUno') canvasRef!: ElementRef<HTMLCanvasElement>;

  private chart!: Chart<'doughnut'>;
  private sub!: Subscription;
  private socketGrafico = inject(SocketGraficoService);
  private resizeObserver!: ResizeObserver;

  private readonly COLORS = [
    'rgb(255, 99, 132)',
    'rgb(255, 159, 64)',
    'rgb(255, 205, 86)',
    'rgb(75, 192, 192)',
    'rgb(54, 162, 235)',
    'rgb(153, 102, 255)',
    'rgb(201, 203, 207)',
    'rgb(255, 99, 71)',
    'rgb(60, 179, 113)',
  ];

  ngAfterViewInit(): void {
    this.inicializarChart();
    this.escucharDatos();
    this.observarTamano();
  }

  inicializarChart(): void {
    const config: ChartConfiguration<'doughnut', number[], string> = {
      type: 'doughnut',
      data: {
        labels: [],
        datasets: [
          {
            label: 'Venta por plato',
            data: [],
            backgroundColor: this.COLORS,
          },
        ],
      },
      options: {
        responsive: false,
        maintainAspectRatio: false,
        plugins: {
          legend: { position: 'top' },
          title: {
            display: true,
            text: 'Platos más vendidos',
          },
        },
      },
    };

    this.chart = new Chart(this.canvasRef.nativeElement, config);

    this.ajustarTamano();
  }

  private ajustarTamano(): void {
    const contenedor = this.canvasRef.nativeElement.parentElement;
    if (contenedor && this.chart) {
      this.chart.resize(contenedor.clientWidth, contenedor.clientHeight);
    }
  }

  private observarTamano(): void {
    this.resizeObserver = new ResizeObserver(() => {
      this.ajustarTamano();
    });
    this.resizeObserver.observe(this.canvasRef.nativeElement.parentElement!);
  }

  escucharDatos(): void {
    this.sub = this.socketGrafico
      .getGraficoUno$()
      .pipe(
        filter((data) => !!data),
        auditTime(800),
      )

      .subscribe((data: GraficoUnoDto[] | null) => {
        console.log('Datos grafico uno:', data);
        if (data && this.chart) {
          this.chart.data.labels = data.map((gr) => gr.nombrePlato);
          this.chart.data.datasets[0].data = data.map(
            (gr) => gr.cantidadPlatos,
          );
          this.chart.update('none');
        }
      });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
    this.chart?.destroy();
  }
}
