
export interface Plato {
  idPlato: number;
  nombre: string;
  precio: number;
  stock: number;
  tipoPlato: string;
  tipoPlayoDescripcion: string;
  imagen: string | null;
  activo: boolean;
  etiquetas: Etiqueta[];
}

export interface PlatoRequest {
  nombre: string;
  precio: number;
  stock: number;
  tipoPlato: string;
  imagen: string | null;
  imagenBase64?: string;
}

export interface PlatoResponse {
  idPlato: number;
  nombre: string;
  precio: number;
  stock: number;
  tipoPlato: string;
  tipoPlayoDescripcion: string;
  imagen: string | null;
  activo: boolean;
  etiquetas: EtiquetaResponse[];
}

export interface Etiqueta {
  idEtiqueta: number;
  nombre: string;
  descripcion: string;
  activo: string;
  fechaRegistro: string;
}

export interface EtiquetaRequest {
  nombre: string;
  descripcion: string;
}

export interface EtiquetaResponse {
  idEtiqueta: number;
  nombre: string;
  descripcion: string;
  activo: string;
  fechaRegistro: string;
}

export interface PlatoEtiquetaRequest {
  idPlato: number;
  idsEtiquetas: number[];
}

export enum TipoPlato {
  ENTRADA = 'E',
  SEGUNDO = 'S',
  POSTRE = 'P',
  BEBIDA = 'B'
}

export const TIPOS_PLATO = [
  { value: 'E', label: 'Entrada' },
  { value: 'S', label: 'Segundo' },
  { value: 'P', label: 'Postre' },
  { value: 'B', label: 'Bebida' }
];