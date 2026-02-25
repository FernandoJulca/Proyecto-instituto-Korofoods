
import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { enviroment } from '../../../enviroments/enviroment';
import { MesaResponse, MesaRequest } from '../models/mesa.model';

@Injectable({
  providedIn: 'root'
})
export class MesaService {

  private apiUrl = `${enviroment.apigateway}/mesa`;


  constructor(private http: HttpClient) {}

  // CRUD de Mesas
  
  listarTodas(): Observable<MesaResponse[]> {
    return this.http.get<MesaResponse[]>(this.apiUrl)
      .pipe(catchError(this.handleError));
  }

  listarActivas(): Observable<MesaResponse[]> {
    return this.http.get<MesaResponse[]>(`${this.apiUrl}/activas`)
      .pipe(catchError(this.handleError));
  }

  listarPorZona(zona: string): Observable<MesaResponse[]> {
    return this.http.get<MesaResponse[]>(`${this.apiUrl}/zona/${zona}`)
      .pipe(catchError(this.handleError));
  }

  listarPorEstado(estado: string): Observable<MesaResponse[]> {
    return this.http.get<MesaResponse[]>(`${this.apiUrl}/estado/${estado}`)
      .pipe(catchError(this.handleError));
  }

  buscarPorId(id: number): Observable<MesaResponse> {
    return this.http.get<MesaResponse>(`${this.apiUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }

  crear(mesa: MesaRequest): Observable<MesaResponse> {
    return this.http.post<MesaResponse>(this.apiUrl, mesa)
      .pipe(catchError(this.handleError));
  }

  actualizar(id: number, mesa: MesaRequest): Observable<MesaResponse> {
    return this.http.put<MesaResponse>(`${this.apiUrl}/${id}`, mesa)
      .pipe(catchError(this.handleError));
  }

  cambiarEstado(id: number, nuevoEstado: string): Observable<MesaResponse> {
    return this.http.patch<MesaResponse>(`${this.apiUrl}/${id}/estado/${nuevoEstado}`, {})
      .pipe(catchError(this.handleError));
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }

  // Error Handler
  
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
}