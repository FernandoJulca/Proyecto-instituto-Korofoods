export interface ReservaRequest {

    idUsuario: number;
    idMesa: number;
    fechaHora: string;
    idEvento: number | null; // null si es reserva normal
    observaciones: string;

}