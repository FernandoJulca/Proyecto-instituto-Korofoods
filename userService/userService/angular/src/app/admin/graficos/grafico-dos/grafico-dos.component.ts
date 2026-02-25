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

@Component({
  selector: 'app-grafico-dos',
  standalone: true,
  imports: [],
  templateUrl: './grafico-dos.component.html',
  styleUrl: './grafico-dos.component.css',
})
export class GraficoDosComponent implements AfterViewInit, OnDestroy {
  @ViewChild('graficoDos') canvasRef!: ElementRef<HTMLCanvasElement>;

  private chart!: Chart<'doughnut'>;
  private sub!: Subscription;
  private resizeObserver!: ResizeObserver;
  private socketGrafico = inject(SocketGraficoService);

  ngAfterViewInit(): void {
    this.inicializarChart();
    this.escucharDatos();
    this.observarTamano();
  }

  inicializarChart(): void {
    const config: ChartConfiguration<'doughnut'> = {
      type: 'doughnut',
      data: {
        labels: ['Con Evento', 'Sin Evento'],
        datasets: [
          {
            data: [0, 0],
            backgroundColor: ['rgb(99, 102, 241)', 'rgb(229, 231, 235)'],
            borderWidth: 0,
            hoverOffset: 8,
          },
        ],
      },
      options: {
        responsive: false,
        maintainAspectRatio: false,
        animation: false,
        plugins: {
          legend: { position: 'bottom' },
          title: { display: true, text: 'Reservas por Evento' },
        },
        cutout: '70%',
      },
    };

    this.chart = new Chart(this.canvasRef.nativeElement, config);
    this.ajustarTamano();
  }

  escucharDatos(): void {
    this.sub = this.socketGrafico
      .getGraficoDos$()
      .pipe(
        filter((data) => !!data),
        auditTime(800),
      )
      .subscribe((data: any) => {
        if (data && this.chart) {
          this.chart.data.datasets[0].data = [data.conEvento, data.sinEvento];
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
