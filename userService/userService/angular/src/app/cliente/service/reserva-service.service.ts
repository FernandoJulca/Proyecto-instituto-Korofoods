import { Injectable } from '@angular/core';
import { enviroment } from '../../../enviroments/enviroment';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { ReservaRequest } from '../../shared/request/ReservaRequest';
import { Observable } from 'rxjs';
import { ResultadoResponse } from '../../shared/dto/ResultadoResponse';
import { Reserva } from '../../shared/model/reserva.model';
import { ReservaResponseDTO } from '../../shared/dto/ReservaResponseDTO';
import { RecepcionistaCountsDTO } from '../../shared/dto/RecepcionistaCountsDTO';
import { ReservaAsistidaDTO } from '../../shared/dto/ReservaAsistidaDTO';

export interface ReporteReservasRequest {
  fechaInicio: string; // formato: YYYY-MM-DD
  fechaFin: string;
  estado?: string; // PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA
  zona?: string; // Z1, Z2, Z3, Z4
}

@Injectable({
  providedIn: 'root',
})
export class ReservaServiceService {
  private baseUrl = `${enviroment.apigateway}/reserva`;

  constructor(private http: HttpClient) {}

  crearReserva(reserva: ReservaRequest): Observable<ResultadoResponse<number>> {
    return this.http.post<ResultadoResponse<number>>(
      `${this.baseUrl}/registro`,
      reserva,
    );
  }

  validarReserva(
    mesaId: number,
    fechaHora: string, // ISO: '2026-02-01T18:30:00'
    esEvento: boolean = false,
  ): Observable<ResultadoResponse<boolean>> {
    const params = {
      mesaId,
      fechaHora,
      esEvento,
    };

    return this.http.get<ResultadoResponse<boolean>>(
      `${this.baseUrl}/ocupada`,
      { params },
    );
  }

  obtenerSlotsDisponibles(
    mesaId: number,
    desde: string, // ISO
    hasta: string, // ISO
    eventoId?: number,
  ): Observable<ResultadoResponse<string[]>> {
    let params: any = {
      mesaId,
      desde,
      hasta,
    };

    if (eventoId) {
      params.eventoId = eventoId;
    }

    return this.http.get<ResultadoResponse<string[]>>(
      `${this.baseUrl}/slots-disponibles`,
      { params },
    );
  }

  // LISTAR RESERVAS DEL CLIENTE
  listarMisReservas(
    idUsuario: number,
  ): Observable<ResultadoResponse<ReservaResponseDTO[]>> {
    return this.http.get<ResultadoResponse<ReservaResponseDTO[]>>(
      `${this.baseUrl}/mis-reservas/${idUsuario}`,
    );
  }

  // CANCELAR RESERVA
  cancelarReserva(idReserva: number): Observable<ResultadoResponse<number>> {
    return this.http.patch<ResultadoResponse<number>>(
      `${this.baseUrl}/cancelar/${idReserva}`,
      {}, // PATCH necesita body, aunque esté vacío
    );
  }

  // NUEVO: Generar reporte de reservas en PDF
  generarReporteReservas(request: ReporteReservasRequest): Observable<Blob> {
    const params = new HttpParams()
      .set('fechaInicio', request.fechaInicio)
      .set('fechaFin', request.fechaFin);

    return this.http.get(`${this.baseUrl}/reportes/reservas/todas`, {
      params,
      responseType: 'blob',
    });
  }
  // GET /reserva/dashboard/recepcionista/counts
  obtenerCountsRecepcionista(): Observable<RecepcionistaCountsDTO> {
    return this.http.get<RecepcionistaCountsDTO>(
      `${this.baseUrl}/dashboard/recepcionista/counts`,
    );
  }

  // GET /reserva/dashboard/recepcionista/asistidas/hoy
  listarReservasAsistidasHoy(): Observable<
    ResultadoResponse<ReservaAsistidaDTO[]>
  > {
    return this.http.get<ResultadoResponse<ReservaAsistidaDTO[]>>(
      `${this.baseUrl}/dashboard/recepcionista/asistidas/hoy`,
    );
  }
}
