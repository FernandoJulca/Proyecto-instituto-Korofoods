export interface CodigoVerificacionResponse {
  mensaje: string;
  fechaExpiracion: string; // LocalDateTime → string ISO
  tipoEnvio: string;
}
