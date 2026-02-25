import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { GraficoSeisDto } from '../../../shared/dto/graficoSeisDto.model';
import { CommonModule } from '@angular/common';
import { filter, Subscription } from 'rxjs';
import { SocketGraficoService } from '../../service/socket-grafico.service';

@Component({
  selector: 'app-grafico-seis',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './grafico-seis.component.html',
  styleUrl: './grafico-seis.component.css',
})
export class GraficoSeisComponent implements OnInit, OnDestroy {
  datos: GraficoSeisDto[] = [];
  private sub!: Subscription;
  private socketGrafico = inject(SocketGraficoService);

  ngOnInit(): void {
    this.sub = this.socketGrafico
      .getGraficoSeis$()
      .pipe(filter((data) => !!data))
      .subscribe((data: GraficoSeisDto[]) => {
        this.datos = data;
      });
  }

  getEstrellas(promedio: number): { rellena: boolean }[] {
    return Array.from({ length: 5 }, (_, i) => ({
      rellena: i < Math.round(promedio),
    }));
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }
}
