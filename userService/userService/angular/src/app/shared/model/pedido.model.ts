import { EstadoPedido } from '../enums/estadoPedido.enum';
import { Mesa } from './mesa.model';
import { Reserva } from './reserva.model';
import { Usuario } from './usuario.model';

export interface Pedido {
  idPedido: number;
  idMesa: Mesa;
  idUsuario: Usuario; // Cliente que realiza el pedido
  idReserva: Reserva;
  fechaHora: string;
  subTotal: number;
  total: number;
  estado: EstadoPedido;
}
