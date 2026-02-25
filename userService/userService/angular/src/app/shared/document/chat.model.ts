export interface Chat {
  id: string;
  emisorId: number;
  receptorId: number;
  ultimoMensaje: string;
  fechaUltimoMensaje: Date;
}
