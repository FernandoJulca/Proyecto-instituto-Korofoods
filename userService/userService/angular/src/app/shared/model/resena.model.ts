import { EstadoResena } from "../enums/estadoResena.enum";
import { TipoEntidad } from "../enums/tipoEntidad.enum";
import { Usuario } from "./usuario.model";

export interface Resena {
    idResena: number;
    idUsuario: Usuario;
    tipoEntidad: TipoEntidad;
    idEntidad: number;
    calificacion: number;
    comentario: string;
    fechaRegistro: string;
    estado: EstadoResena;
}