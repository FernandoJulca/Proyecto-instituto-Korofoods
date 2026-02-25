// shared/dto/PagoDto.ts

export interface CrearPagoRequest {
  idReserva?: number | null;
  idPedido?: number | null;
  idUsuario: number;
  tipoPago: string; // "DR" o "PP"
  monto: number;
  metodoPago: string; // "YAPE", "PLIN", "EFECTIVO", "TARJETA"
  observaciones?: string;
}

export interface SubirCapturaRequest {
  idPago: number;
  imagenBase64: string; // Imagen en Base64
  metodoPago: string; // "YAPE" o "PLIN"
}



export interface PagoResponse {
  idPago: number;
  idReserva: number | null;
  idPedido: number | null;
  idUsuario: number;
  tipoPago: string;
  tipoPagoDescripcion: string;
  monto: number;
  metodoPago: string;
  metodoPagoDescripcion: string;
  fechaPago: string | null;
  estado: string;
  estadoDescripcion: string;
  observaciones: string | null;
  referenciaPago: string;
  fechaCreacion: string;
  fechaExpiracion: string;
  codigoOperacion: string | null;
  urlCaptura?: string;
  hashImagen?: string;
  montoDetectado?: number;
  fechaDetectada?: string;
  motivoRechazo?: string;
}

export enum EstadoPago {
  PEND = 'PENDIENTE',
  PAG = 'PAGADO',
  RECH = 'RECHAZADO',
  ANU = 'ANULADO',
  EXP = 'EXPIRADO'
}