import { DetallePedidoRequestDTO } from "./DetallePedidoRequestDTO";

export interface PedidoRequestoDto{
    idMesa:number;
    idUsuario:number;
    idReserva:number;
    detalles: DetallePedidoRequestDTO[]
}