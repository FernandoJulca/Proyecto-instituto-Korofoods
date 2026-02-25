export interface ReservaAsistidaDTO {
  idReserva: number;
  nombreCliente: string;
  tipoReserva: string;
  fechaReserva: string;
  observaciones: string | null;
  mesa: number;
  zona: string | null;
  evento: string | null;
  tematica: string | null;
}
