import { TipoPlato } from "../enums/tipoPlato.enum";

export interface Plato{
    idPlato: number;
    precio: number;
    stock: number;
    tipoPlato: TipoPlato;
    imagen: string;
    
}