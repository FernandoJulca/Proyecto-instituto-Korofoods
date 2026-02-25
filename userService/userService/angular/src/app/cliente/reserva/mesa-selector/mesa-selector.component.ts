import { CommonModule } from '@angular/common';
import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { MesaDto } from '../../../shared/dto/MesaDto';
import { EventoConMesaDto } from '../../../shared/dto/EventoConMesaDto';

// Tipo unión para trabajar con ambos tipos
export type MesaItem = MesaDto | EventoConMesaDto;


@Component({
  selector: 'app-mesa-selector',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './mesa-selector.component.html',
  styleUrls: ['./mesa-selector.component.css'],
})
export class MesaSelectorComponent implements OnInit {
  @Input() mesas: MesaItem[] = [];
  @Input() isLoading: boolean = false;
  @Input() tipoVista: 'normal' | 'evento' = 'normal';
  @Input() mensaje: string = '';

  @Output() mesaSeleccionada = new EventEmitter<MesaItem>();

  mesaActiva: number | null = null;

  constructor() {}

  ngOnInit(): void {}

  seleccionarMesa(mesa: MesaItem): void {
    if (this.isMesaDto(mesa)) {
      if (mesa.estado !== 'LIBRE') return;
      this.mesaActiva = mesa.idMesa;
    } else {
      this.mesaActiva = mesa.idMesa;
    }

    this.mesaSeleccionada.emit(mesa);
  }

  isMesaDto(mesa: MesaItem): mesa is MesaDto {
    return 'estado' in mesa && 'tipo' in mesa;
  }

  isEventoConMesaDto(mesa: MesaItem): mesa is EventoConMesaDto {
    return 'nombre' in mesa && 'tematica' in mesa;
  }

  getMesaEstado(mesa: MesaItem): string {
    if (this.isMesaDto(mesa)) {
      return mesa.estado;
    }
    return 'DISPONIBLE'; // Para eventos, asumimos que están disponibles
  }

  getZona(mesa: MesaItem): string {
    if (this.isMesaDto(mesa)) {
      return mesa.tipo;
    }
    return mesa.zona;
  }

  getNumeroMesa(mesa: MesaItem): number {
    return mesa.numeroMesa;
  }

  getCapacidad(mesa: MesaItem): number {
    return mesa.capacidad;
  }

  getIdMesa(mesa: MesaItem): number {
    return mesa.idMesa;
  }

  formatearFecha(fecha: string): string {
    const date = new Date(fecha);
    const opciones: Intl.DateTimeFormatOptions = {
      day: '2-digit',
      month: 'short',
      hour: '2-digit',
      minute: '2-digit',
    };
    return date.toLocaleDateString('es-ES', opciones);
  }

  getEstadoClass(estado: string): string {
    switch (estado) {
      case 'LIBRE':
      case 'DISPONIBLE':
        return 'estado-libre';
      case 'OCUPADA':
        return 'estado-ocupada';
      default:
        return '';
    }
  }

  getZonaColor(zona: string): string {
    const colores: { [key: string]: string } = {
      Z1: '#FF6B6B',
      Z2: '#4ECDC4',
      Z3: '#FFE66D',
      Z4: '#A8E6CF',
      Z5: '#C7B3E5',
    };
    return colores[zona] || '#C67C4E';
  }
}
