import { Component, inject } from '@angular/core';
import { GraficoCincoDto } from '../../../shared/dto/graficoCincoDto.model';
import { filter, Subscription } from 'rxjs';
import { SocketGraficoService } from '../../service/socket-grafico.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-grafico-cinco',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './grafico-cinco.component.html',
  styleUrl: './grafico-cinco.component.css',
})
export class GraficoCincoComponent {
  datos: GraficoCincoDto[] = [];
  private sub!: Subscription;
  private socketGrafico = inject(SocketGraficoService);

  ngOnInit(): void {
    this.sub = this.socketGrafico
      .getGraficoCinco$()
      .pipe(filter((data) => !!data))
      .subscribe((data: GraficoCincoDto[]) => {
        this.datos = data;
      });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }
}
