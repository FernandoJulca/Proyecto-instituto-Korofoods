import { EstadoMesa } from "../enums/estadoMesa.enum";
import { Zona } from "../enums/Zona";

export interface Mesa{
    idMesa: number;
    numeroMesa: number;
    capacidad: number;
    tipo: Zona;
    estado: EstadoMesa; 
}