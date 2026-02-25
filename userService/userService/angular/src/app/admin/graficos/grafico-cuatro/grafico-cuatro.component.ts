import {
  AfterViewInit,
  Component,
  ElementRef,
  inject,
  OnDestroy,
  ViewChild,
} from '@angular/core';
import { Chart, ChartConfiguration } from 'chart.js';
import { filter, auditTime, Subscription } from 'rxjs';
import { SocketGraficoService } from '../../service/socket-grafico.service';
@Component({
  selector: 'app-grafico-cuatro',
  standalone: true,
  imports: [],
  templateUrl: './grafico-cuatro.component.html',
  styleUrl: './grafico-cuatro.component.css',
})
export class GraficoCuatroComponent implements AfterViewInit, OnDestroy {
  @ViewChild('graficoCuatro') canvasRef!: ElementRef<HTMLCanvasElement>;

  private chart!: Chart<'bar'>;
  private sub!: Subscription;
  private resizeObserver!: ResizeObserver;
  private socketGrafico = inject(SocketGraficoService);

  ngAfterViewInit(): void {
    this.inicializarChart();
    this.escucharDatos();
    this.observarTamano();
  }

  inicializarChart(): void {
    const config: ChartConfiguration<'bar'> = {
      type: 'bar',
      data: {
        labels: [],
        datasets: [
          {
            label: 'Reservas',
            data: [],
            backgroundColor: [
              'rgba(99, 102, 241, 0.8)',
              'rgba(168, 85, 247, 0.8)',
              'rgba(59, 130, 246, 0.8)',
              'rgba(16, 185, 129, 0.8)',
              'rgba(245, 158, 11, 0.8)',
            ],
            borderRadius: 8,
            borderSkipped: false,
          },
        ],
      },
      options: {
        responsive: false,
        maintainAspectRatio: false,
        animation: false,
        indexAxis: 'y',
        plugins: {
          legend: { display: false },
          title: { display: true, text: 'Reservas por Evento' },
        },
        scales: {
          x: {
            beginAtZero: true,
            ticks: { stepSize: 1 },
            grid: { color: 'rgba(0,0,0,0.05)' },
          },
          y: {
            grid: { display: false },
            ticks: {
              font: { size: 11 },
            },
          },
        },
      },
    };

    this.chart = new Chart(this.canvasRef.nativeElement, config);
    this.ajustarTamano();
  }

  escucharDatos(): void {
    this.sub = this.socketGrafico
      .getGraficoCuatro$()
      .pipe(
        filter((data) => !!data),
        auditTime(800),
      )
      .subscribe((data: any[]) => {
        if (data && this.chart) {
          this.chart.data.labels = data.map((d) => d.nombre);
          this.chart.data.datasets[0].data = data.map((d) => d.cantidad);
          this.chart.update('none');
        }
      });
  }

  private ajustarTamano(): void {
    const contenedor = this.canvasRef.nativeElement.parentElement;
    if (contenedor && this.chart) {
      this.chart.resize(contenedor.clientWidth, contenedor.clientHeight);
    }
  }

  private observarTamano(): void {
    this.resizeObserver = new ResizeObserver(() => this.ajustarTamano());
    this.resizeObserver.observe(this.canvasRef.nativeElement.parentElement!);
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
    this.chart?.destroy();
    this.resizeObserver?.disconnect();
  }
}
