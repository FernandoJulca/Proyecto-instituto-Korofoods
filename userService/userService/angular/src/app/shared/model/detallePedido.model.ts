import { EstadoDetallePedido } from '../enums/estadoDetallePedido.enum';
import { Pedido } from './pedido.model';
import { Plato } from './plato.model';

export interface DetallePedido {
  idDetalle: number;
  idPedido: Pedido;
  idPlato: Plato;
  cantidad: number;
  estado: EstadoDetallePedido;
  precioUnitario: number;
  subTotal: number;
}
