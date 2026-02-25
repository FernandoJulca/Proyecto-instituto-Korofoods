export interface DetallePedidoMeseroResponse {
  nombres: string;
  apePaterno: string;
  apeMaterno: string;

  pedidosTotales: number;
  clientesTotales: number;
  pedidosCompletados: number;
}
