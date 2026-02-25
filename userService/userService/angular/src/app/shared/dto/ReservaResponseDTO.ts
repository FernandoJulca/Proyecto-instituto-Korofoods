import { EstadoReserva } from "../enums/estadoReserva.enum";
import { TipoReserva } from "../enums/tipoReserva.enum";


export interface ReservaResponseDTO {
  idReserva: number;

  // Cliente
  nombreCli: string;
  apellidoPa: string;
  apellidoMa: string;

  // Mesa
  numMesa: number;
  capacidad: number;
  zona: string;

  // Reserva
  tipoReserva: TipoReserva;
  fechaHora: string; // LocalDateTime → string ISO
  estado: EstadoReserva;
  observaciones: string;

  // Evento (opcional)
  idEvento?: number;
  nombreEvento?: string;
  fechaInicio?: string;
  fechaFin?: string;
}
