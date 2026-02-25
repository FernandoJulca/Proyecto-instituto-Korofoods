export interface RecepcionistaCountsDTO {
  reservasHoy: number;
  reservasAsistidas: number;
  reservasPendientes: number; // reservasHoy - reservasAsistidas
  reservasTomorrow: number;
}