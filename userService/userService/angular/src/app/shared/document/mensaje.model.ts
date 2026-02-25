export interface Mensaje {
  id?: string;
  chatId: string;
  emisorId: number;
  receptorId: number;
  contenido: string;
  fechaMandado: Date;
  leido: boolean;
}
