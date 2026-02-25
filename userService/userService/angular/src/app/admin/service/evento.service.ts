// services/evento.service.ts

import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { enviroment } from '../../../enviroments/enviroment';
import {
  EventoResponse,
  EventoRequest,
  TematicaResponse,
} from '../models/evento.model';

@Injectable({
  providedIn: 'root',
})
export class EventoService {
  private apiUrl = `${enviroment.apigateway}/eventos`;
  private tematicaUrl = `${enviroment.apigateway}/tematicas`;

  constructor(private http: HttpClient) {}

  // Eventos
  listarTodos(): Observable<EventoResponse[]> {
    return this.http
      .get<EventoResponse[]>(this.apiUrl)
      .pipe(catchError(this.handleError));
  }

  listarActivos(): Observable<EventoResponse[]> {
    return this.http
      .get<EventoResponse[]>(`${this.apiUrl}/activos`)
      .pipe(catchError(this.handleError));
  }

  listarEventosFuturos(): Observable<EventoResponse[]> {
    return this.http
      .get<EventoResponse[]>(`${this.apiUrl}/futuros`)
      .pipe(catchError(this.handleError));
  }

  buscarPorId(id: number): Observable<EventoResponse> {
    return this.http
      .get<EventoResponse>(`${this.apiUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }

  crear(evento: EventoRequest): Observable<EventoResponse> {
    return this.http
      .post<EventoResponse>(this.apiUrl, evento)
      .pipe(catchError(this.handleError));
  }

  actualizar(id: number, evento: EventoRequest): Observable<EventoResponse> {
    return this.http
      .put<EventoResponse>(`${this.apiUrl}/${id}`, evento)
      .pipe(catchError(this.handleError));
  }

  eliminar(id: number): Observable<void> {
    return this.http
      .delete<void>(`${this.apiUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }

  // Temáticas
  listarTematicas(): Observable<TematicaResponse[]> {
    return this.http
      .get<TematicaResponse[]>(`${this.tematicaUrl}/activas`)
      .pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Ocurrió un error desconocido';

    if (error.error instanceof ErrorEvent) {
      // Error del lado del cliente
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Error del lado del servidor
      if (error.error && error.error.message) {
        errorMessage = error.error.message;
      } else if (error.status === 0) {
        errorMessage = 'No se pudo conectar con el servidor';
      } else {
        errorMessage = `Error ${error.status}: ${error.message}`;
      }
    }

    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }

  generarReporteEventos(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/reporte/eventos`, {
      responseType: 'blob'
    });
  }
}
