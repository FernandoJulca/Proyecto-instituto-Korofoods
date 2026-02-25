import { EstadoPago } from "../enums/estadoPago.enum";
import { MetodoPago } from "../enums/metodoPago.enum";
import { Pedido } from "./pedido.model";
import { Reserva } from "./reserva.model";
import { Usuario } from "./usuario.model";

export interface Pago{
    idPago: number;
    idReserva: Reserva;
    idPedido: Pedido;
    idUsuario: Usuario; // Es el mesero que procesa el pago ya que el cliente estara relacionado a la reserva
                        //  y es irrelevante en este contexto
    fechaHora: string;
    monto: number;
    metodoPago: MetodoPago;
    estadoPago: EstadoPago;
}