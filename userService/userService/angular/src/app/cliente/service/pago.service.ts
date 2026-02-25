import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { enviroment } from '../../../enviroments/enviroment';
import {
  CrearPagoRequest,
  SubirCapturaRequest,
  PagoResponse,
} from '../../cliente/pago/pagoDto';

@Injectable({
  providedIn: 'root',
})
export class PagoService {
  private apiUrl = `${enviroment.apigateway}/pago`;

  constructor(private http: HttpClient) {}

  crearPago(request: CrearPagoRequest): Observable<PagoResponse> {
    console.log(
      '📤 [crearPago] Body enviado:',
      JSON.stringify(request, null, 2),
    );

    return this.http.post<PagoResponse>(this.apiUrl, request).pipe(
      tap((response) => {
        console.log(
          '📥 [crearPago] Response recibido:',
          JSON.stringify(response, null, 2),
        );
      }),
      catchError(this.handleError),
    );
  }

  subirCaptura(request: SubirCapturaRequest): Observable<PagoResponse> {
    console.log(
      '📤 [subirCaptura] Body enviado:',
      JSON.stringify(request, null, 2),
    );

    return this.http
      .post<PagoResponse>(`${this.apiUrl}/subir-captura`, request)
      .pipe(
        tap((response) => {
          console.log(
            '📥 [subirCaptura] Response recibido:',
            JSON.stringify(response, null, 2),
          );
        }),
        catchError(this.handleError),
      );
  }

  buscarPorId(id: number): Observable<PagoResponse> {
    console.log('📤 [buscarPorId] ID consultado:', id);

    return this.http.get<PagoResponse>(`${this.apiUrl}/${id}`).pipe(
      tap((response) => {
        console.log(
          '📥 [buscarPorId] Response recibido:',
          JSON.stringify(response, null, 2),
        );
      }),
      catchError(this.handleError),
    );
  }

  buscarPorReferencia(referencia: string): Observable<PagoResponse> {
    console.log('📤 [buscarPorReferencia] Referencia consultada:', referencia);

    return this.http
      .get<PagoResponse>(`${this.apiUrl}/referencia/${referencia}`)
      .pipe(
        tap((response) => {
          console.log(
            '📥 [buscarPorReferencia] Response recibido:',
            JSON.stringify(response, null, 2),
          );
        }),
        catchError(this.handleError),
      );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Ocurrió un error desconocido';

    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      if (error.error && error.error.message) {
        errorMessage = error.error.message;
      } else if (error.status === 0) {
        errorMessage = 'No se pudo conectar con el servidor';
      } else if (error.status === 404) {
        errorMessage = 'Recurso no encontrado';
      } else if (error.status === 400) {
        errorMessage = error.error?.message || 'Datos inválidos';
      } else if (error.status === 500) {
        errorMessage = 'Error interno del servidor';
      } else {
        errorMessage = `Error ${error.status}: ${error.message}`;
      }
    }

    console.error('❌ [PagoService] Error:', errorMessage, error);
    return throwError(() => new Error(errorMessage));
  }

  generarReporteIngresos(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/reporte/ingresos`, {
      responseType: 'blob'
    });
  }
}
