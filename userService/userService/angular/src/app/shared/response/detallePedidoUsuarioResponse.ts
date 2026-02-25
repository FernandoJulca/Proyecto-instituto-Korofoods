export interface DetallePedidoUsuarioResponse {
  nombres: string;
  apePaterno: string;
  apeMaterno: string;

  entregados: number;
  pedidos: number;
  cancelados: number;
}
