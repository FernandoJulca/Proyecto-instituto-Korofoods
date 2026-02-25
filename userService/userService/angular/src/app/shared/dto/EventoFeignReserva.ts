export interface EventoFeignReserva {
    idEvento: number;
    nombre: string;
    descripcion: string;
    tematica: string;
    fechaInicio: string; // LocalDateTime → string ISO
    fechaFin: string; // LocalDateTime → string ISO
    aforo: number;
    imagen: string;
}
