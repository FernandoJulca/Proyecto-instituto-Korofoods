export interface Mesa {
  idMesa: number;
  numeroMesa: number;
  capacidad: number;
  zona: string;
  zonaDescripcion: string;
  estado: string;
  estadoDescripcion: string;
  activo: boolean;
}

export interface MesaRequest {
  numeroMesa: number;
  capacidad: number;
  zona: string;
  estado: string;
}

export interface MesaResponse {
  idMesa: number;
  numeroMesa: number;
  capacidad: number;
  zona: string;
  zonaDescripcion: string;
  estado: string;
  estadoDescripcion: string;
  activo: boolean;
}

export enum Zona {
  Z1 = 'Z1',
  Z2 = 'Z2'
}

export enum EstadoMesa {
  LIBRE = 'LIBRE',
  ASIGNADA = 'ASIGNADA',
  OCUPADA = 'OCUPADA'
}

export const ZONAS = [
  { value: 'Z1', label: 'Zona 1' },
  { value: 'Z2', label: 'Zona 2' }
];

export const ESTADOS_MESA = [
  { value: 'LIBRE', label: 'Libre' },
  { value: 'ASIGNADA', label: 'Asignada' },
  { value: 'OCUPADA', label: 'Ocupada' }
];