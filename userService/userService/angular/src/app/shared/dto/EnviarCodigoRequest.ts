export interface EnviarCodigoRequest {
  reservaId: number;
  tipoEnvio: 'SMS' | 'EMAIL';
}
