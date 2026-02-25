import { TipoEntidad } from "../enums/tipoEntidad.enum";

export interface ResenaRequest {
  idUsuario: number;
  tipoEntidad: TipoEntidad; 
  idEntidad: number;
  calificacion: number;
  comentario: string;
}