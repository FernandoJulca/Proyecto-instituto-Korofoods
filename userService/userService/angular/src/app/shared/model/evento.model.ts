import { Tematica } from './tematica.model';

export interface Evento {
  idEvento: number;
  nombre: string;
  descripcion: string;
  tematica: Tematica;
  fecha: string;
  hora: string;
  precio: number;
  aforo: number;
  cupos: number;
  imagen: string;
}
