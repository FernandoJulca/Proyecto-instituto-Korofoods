export interface EventoConMesaDto {
    // Datos de EventoMesa
    idEventoMesa: number;

    // Datos de Evento
    nombre: string;
    descripcion: string;
    tematica: string;
    fechaInicio: string;
    fechaFin: string;
    imagen: string | null;
    activo: boolean;

    // Datos de Mesa (desde Feign)
    idMesa: number;
    numeroMesa: number;
    capacidad: number;
    zona: string;
}