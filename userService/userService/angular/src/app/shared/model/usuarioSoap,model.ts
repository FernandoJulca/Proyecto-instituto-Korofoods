export interface UsuarioSoap {
  idUsuario?: number;
  nombres: string;
  apePaterno: string;
  apeMaterno: string;
  correo: string;
  clave?: string;
  tipoDoc: 'DNI' | 'PAS' | 'CDX' | 'CMP';
  nroDoc: string;
  direccion?: string;
  telefono?: string;
  idDistrito?: number;
  idRol: number;
  nombreRol?: string;
  activo?: boolean;
  fechaRegistro?: string;
}

export interface RespuestaSoap {
  exitoso: boolean;
  mensaje: string;
  codigo?: string;
}