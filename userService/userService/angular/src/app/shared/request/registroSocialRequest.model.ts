import { TipoDocumento } from '../enums/tipoDocumento.enum';

export interface RegistroSocialRequest {
  tempToken: string;
  nombres: string;
  correo: string;
  provider: string;

  imagen: string;
  apePaterno: string;
  apeMaterno: string;
  tipoDocumento: TipoDocumento;
  nroDoc: string;
  telefono: string;
  direccion: string;
  idDistrito: number;
  clave: string;
}
