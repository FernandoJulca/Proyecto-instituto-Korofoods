import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { enviroment } from '../../../enviroments/enviroment';
import {
  PlatoResponse,
  PlatoRequest,
  EtiquetaResponse,
  EtiquetaRequest,
  PlatoEtiquetaRequest,
} from '../models/plato.model';

@Injectable({
  providedIn: 'root',
})
export class PlatoService {
  private apiUrl = `${enviroment.apigateway}/platos`;
  private etiquetaUrl = `${enviroment.apigateway}/etiquetas`;
  private platoEtiquetaUrl = `${enviroment.apigateway}/plato-etiquetas`;

  constructor(private http: HttpClient) {}

  // ============ PLATOS ============

  listarTodos(): Observable<PlatoResponse[]> {
    return this.http
      .get<PlatoResponse[]>(this.apiUrl)
      .pipe(catchError(this.handleError));
  }

  listarActivos(): Observable<PlatoResponse[]> {
    return this.http
      .get<PlatoResponse[]>(`${this.apiUrl}/activos`)
      .pipe(catchError(this.handleError));
  }

  listarActivosOrdenados(): Observable<PlatoResponse[]> {
    return this.http
      .get<PlatoResponse[]>(`${this.apiUrl}/ordenados`)
      .pipe(catchError(this.handleError));
  }

  listarPorTipo(tipoPlato: string): Observable<PlatoResponse[]> {
    return this.http
      .get<PlatoResponse[]>(`${this.apiUrl}/tipo/${tipoPlato}`)
      .pipe(catchError(this.handleError));
  }

  buscarPorId(id: number): Observable<PlatoResponse> {
    return this.http
      .get<PlatoResponse>(`${this.apiUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }

  crear(plato: PlatoRequest): Observable<PlatoResponse> {
    return this.http
      .post<PlatoResponse>(this.apiUrl, plato)
      .pipe(catchError(this.handleError));
  }

  actualizar(id: number, plato: PlatoRequest): Observable<PlatoResponse> {
    return this.http
      .put<PlatoResponse>(`${this.apiUrl}/${id}`, plato)
      .pipe(catchError(this.handleError));
  }

  eliminar(id: number): Observable<void> {
    return this.http
      .delete<void>(`${this.apiUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }

  // ============ ETIQUETAS ============

  listarEtiquetas(): Observable<EtiquetaResponse[]> {
    return this.http
      .get<EtiquetaResponse[]>(`${this.etiquetaUrl}/activas`)
      .pipe(catchError(this.handleError));
  }

  crearEtiqueta(etiqueta: EtiquetaRequest): Observable<EtiquetaResponse> {
    return this.http
      .post<EtiquetaResponse>(this.etiquetaUrl, etiqueta)
      .pipe(catchError(this.handleError));
  }

  actualizarEtiqueta(
    id: number,
    etiqueta: EtiquetaRequest,
  ): Observable<EtiquetaResponse> {
    return this.http
      .put<EtiquetaResponse>(`${this.etiquetaUrl}/${id}`, etiqueta)
      .pipe(catchError(this.handleError));
  }

  eliminarEtiqueta(id: number): Observable<void> {
    return this.http
      .delete<void>(`${this.etiquetaUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }

  // ============ PLATO-ETIQUETAS ============

  asignarEtiquetas(request: PlatoEtiquetaRequest): Observable<void> {
    return this.http
      .post<void>(this.platoEtiquetaUrl, request)
      .pipe(catchError(this.handleError));
  }

  // ============ ERROR HANDLER ============

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Ocurrió un error desconocido';

    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
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

  generarReportePlatos(): Observable<Blob> {
    return this.http.get(
      `${this.apiUrl.replace('/menu/feign', '/menu')}/reporte/platos`,
      {
        responseType: 'blob',
      },
    );
  }
}
