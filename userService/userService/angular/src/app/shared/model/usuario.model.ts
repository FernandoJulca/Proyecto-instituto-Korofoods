import { TipoDocumento } from '../enums/tipoDocumento.enum';
import { Distrito } from './distrito.model';
import { Rol } from './rol.model';

export interface Usuario {
  idUsuario?: number;
  nombres: string;
  apePaterno: string;
  apeMaterno: string;
  correo: string;
  clave: string;
  tipoDoc: TipoDocumento;
  nroDoc: string;
  imagen?: string;
  direccion: string;
  telefono: string;
  distrito: Distrito;
  rol?: Rol;
  fechaRegistro?: string;
  estado?: boolean;
  imagenMultipart?: File;
}
