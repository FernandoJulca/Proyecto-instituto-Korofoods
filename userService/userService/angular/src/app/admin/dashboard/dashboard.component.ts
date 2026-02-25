import {
  Component,
  AfterViewInit,
  ViewChild,
  ElementRef,
  OnDestroy,
  inject,
} from '@angular/core';

import { GraficoUnoComponent } from '../graficos/grafico-uno/grafico-uno.component';
import { GraficoDosComponent } from '../graficos/grafico-dos/grafico-dos.component';
import { GraficoTresComponent } from '../graficos/grafico-tres/grafico-tres.component';
import { GraficoCuatroComponent } from '../graficos/grafico-cuatro/grafico-cuatro.component';
import { GraficoCincoComponent } from '../graficos/grafico-cinco/grafico-cinco.component';
import { GraficoSeisComponent } from '../graficos/grafico-seis/grafico-seis.component';

import { GridStack } from 'gridstack';
import { SocketGraficoService } from '../service/socket-grafico.service';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    GraficoUnoComponent,
    GraficoDosComponent,
    GraficoTresComponent,
    GraficoCuatroComponent,
    GraficoCincoComponent,
    GraficoSeisComponent,
    NgIf,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent implements AfterViewInit, OnDestroy {
  @ViewChild('grid') gridEl!: ElementRef;
  grid!: GridStack;

  cargando = true;
  graficosVisibles = {
    uno: false,
    dos: false,
    tres: false,
    cuatro: false,
    cinco: false,
    seis: false,
  };

  mesSeleccionado: number = new Date().getMonth() + 1;

  meses = [
    { valor: 1, nombre: 'Enero' },
    { valor: 2, nombre: 'Febrero' },
    { valor: 3, nombre: 'Marzo' },
    { valor: 4, nombre: 'Abril' },
    { valor: 5, nombre: 'Mayo' },
    { valor: 6, nombre: 'Junio' },
    { valor: 7, nombre: 'Julio' },
    { valor: 8, nombre: 'Agosto' },
    { valor: 9, nombre: 'Septiembre' },
    { valor: 10, nombre: 'Octubre' },
    { valor: 11, nombre: 'Noviembre' },
    { valor: 12, nombre: 'Diciembre' },
  ];

  private socketGrafico = inject(SocketGraficoService);

  ngAfterViewInit() {
    setTimeout(() => {
      this.grid = GridStack.init(
        {
          column: 4,
          cellHeight: 200,
          margin: 15,
          float: true,
          resizable: { handles: 'all' },
        },
        this.gridEl.nativeElement,
      );

      const orden = ['tres', 'uno', 'seis', 'dos', 'cuatro', 'cinco'];
      orden.forEach((grafico, index) => {
        setTimeout(() => {
          (this.graficosVisibles as any)[grafico] = true;
          if (index === orden.length - 1) {
            this.cargando = false;
          }
        }, index * 400);
      });

      setTimeout(
        () => {
          this.socketGrafico.inicializarDashboard(this.mesSeleccionado);
        },
        orden.length * 400 + 200,
      );
    }, 0);
  }

  onMesCambiado(): void {
    this.socketGrafico.inicializarDashboard(this.mesSeleccionado);
  }

  ngOnDestroy(): void {}
}
