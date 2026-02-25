import {
  AfterViewInit,
  Component,
  ElementRef,
  inject,
  OnDestroy,
  ViewChild,
} from '@angular/core';
import { Chart, ChartConfiguration } from 'chart.js';
import { auditTime, filter, Subscription } from 'rxjs';
import { SocketGraficoService } from '../../service/socket-grafico.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-grafico-tres',
  standalone: true,
  imports: [],
  templateUrl: './grafico-tres.component.html',
  styleUrl: './grafico-tres.component.css',
})
export class GraficoTresComponent implements AfterViewInit, OnDestroy {
  @ViewChild('graficoTres') canvasRef!: ElementRef<HTMLCanvasElement>;

  private chart!: Chart<'line'>;
  private sub!: Subscription;
  private resizeObserver!: ResizeObserver;
  private socketGrafico = inject(SocketGraficoService);

  ngAfterViewInit(): void {
    this.inicializarChart();
    this.escucharDatos();
    this.observarTamano();
  }

  inicializarChart(): void {
    const config: ChartConfiguration<'line'> = {
      type: 'line',
      data: {
        labels: ['Efectivo', 'Tarjeta', 'Yape', 'Plin'],
        datasets: [
          {
            label: 'Métodos de Pago',
            data: [0, 0, 0, 0],
            borderColor: 'rgb(99, 132, 255)',
            backgroundColor: 'rgba(99, 132, 255, 0.1)',
            borderWidth: 3,
            pointBackgroundColor: [
              'rgb(75, 192, 192)',
              'rgb(54, 162, 235)',
              'rgb(147, 51, 234)',
              'rgb(59, 130, 246)',
            ],
            pointRadius: 8,
            pointHoverRadius: 10,
            fill: true,
            tension: 0.4,
          },
        ],
      },
      options: {
        responsive: false,
        maintainAspectRatio: false,
        animation: false,
        plugins: {
          legend: { position: 'top' },
          title: { display: true, text: 'Métodos de Pago' },
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: { stepSize: 1 },
            grid: { color: 'rgba(0,0,0,0.05)' },
          },
          x: {
            grid: { display: false },
          },
        },
      },
    };

    this.chart = new Chart(this.canvasRef.nativeElement, config);
    this.ajustarTamano();
  }

  escucharDatos(): void {
    this.sub = this.socketGrafico
      .getGraficoTres$()
      .pipe(
        filter((data) => !!data),
        auditTime(800),
      )
      .subscribe((data: any) => {
        if (data && this.chart) {
          this.chart.data.datasets[0].data = [
            data.pagoEfectivo,
            data.pagoTarjeta,
            data.pagoYape,
            data.pagoPlin,
          ];
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
