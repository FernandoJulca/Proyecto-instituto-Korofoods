export interface DetallePedidoResponse {
  idReserva: number;
  idDetalle: number;
  idPedido: number;
  idPlato: number;
  imagen: string;
  nombre: string;
  cantidad: number;

  estado: string;
  precioUnitario: number;
  subTotal: number;
}
