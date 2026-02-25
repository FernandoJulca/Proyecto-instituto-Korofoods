export interface MesaDto {
    idMesa: number;
    numeroMesa: number;
    capacidad: number;
    tipo: string; // Zona
    estado: 'LIBRE' | 'OCUPADA';
}