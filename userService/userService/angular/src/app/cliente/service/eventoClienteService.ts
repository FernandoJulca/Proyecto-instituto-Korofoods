import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResultadoResponse } from '../../shared/dto/ResultadoResponse';
import { enviroment } from '../../../enviroments/enviroment';
import { EventoDto } from '../../shared/dto/EventoDto';
import { EventoConMesaDto } from '../../shared/dto/EventoConMesaDto';
import { EventoFeignReserva } from '../../shared/dto/EventoFeignReserva';

@Injectable({
  providedIn: 'root',
})
export class EventoClienteService {
  private baseUrl = `${enviroment.apigateway}/evento/feign`;

  constructor(private http: HttpClient) {}

  // GET /evento/feign
  listarEventos(): Observable<ResultadoResponse<EventoDto[]>> {
    return this.http.get<ResultadoResponse<EventoDto[]>>(this.baseUrl);
  }

  // GET /evento/feign/{id}
  obtenerEventoPorId(id: number): Observable<ResultadoResponse<EventoDto>> {
    return this.http.get<ResultadoResponse<EventoDto>>(`${this.baseUrl}/${id}`);
  }

  // GET /evento/feign/validar/{id}
  obtenerEventoValidado(
    id: number,
  ): Observable<ResultadoResponse<EventoFeignReserva>> {
    return this.http.get<ResultadoResponse<EventoFeignReserva>>(
      `${this.baseUrl}/validar/${id}`,
    );
  }

  // GET /evento/feign/mesas/{idEvento}?cantidadPersonas=
  listarMesasPorEvento(
    idEvento: number,
    cantidadPersonas?: number,
  ): Observable<ResultadoResponse<EventoConMesaDto[]>> {
    let params = new HttpParams();

    if (cantidadPersonas !== undefined) {
      params = params.set('cantidadPersonas', cantidadPersonas);
    }

    return this.http.get<ResultadoResponse<EventoConMesaDto[]>>(
      `${this.baseUrl}/mesas/${idEvento}`,
      { params },
    );
  }

  // GET /evento/feign/ocupaciones
  validarOcupacion(
    mesaId: number,
    eventoId: number,
    desde: Date,
    hasta: Date,
  ): Observable<ResultadoResponse<boolean>> {
    let params = new HttpParams()
      .set('mesaId', mesaId)
      .set('eventoId', eventoId)
      .set('desde', desde.toISOString())
      .set('hasta', hasta.toISOString());

    return this.http.get<ResultadoResponse<boolean>>(
      `${this.baseUrl}/ocupaciones`,
      { params },
    );
  }

  // GET /evento/feign/dashboard/hoy
  listarEventosDelDia(): Observable<ResultadoResponse<EventoFeignReserva[]>> {
    return this.http.get<ResultadoResponse<EventoFeignReserva[]>>(
      `${this.baseUrl}/dashboard/hoy`,
    );
  }

  
}