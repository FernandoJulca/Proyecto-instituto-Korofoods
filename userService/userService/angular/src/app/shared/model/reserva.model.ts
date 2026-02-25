import { EstadoReserva } from "../enums/estadoReserva.enum";

export interface Reserva {

    idReserva: number;
    idUsuario: number;
    idMesa: number;
    fechaHora: string;
    estado: EstadoReserva;
    monto: number;
    fechaRegistro: string;
}