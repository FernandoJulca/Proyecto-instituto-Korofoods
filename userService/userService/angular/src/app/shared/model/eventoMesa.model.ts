import { Evento } from "./evento.model";
import { Mesa } from "./mesa.model";

export interface EventoMesa{
    idEventoMesa: number;
    idEvento: Evento;
    idMesa: Mesa;
    fechaDesde: string;
    fechaHasta: string;
}