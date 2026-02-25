import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResenaRequest } from '../../shared/dto/ResenaRequest';
import { ResultadoResponse } from '../../shared/dto/ResultadoResponse';
import { Resena } from '../../shared/model/resena.model';
import { ResenaListResponse } from '../../shared/dto/ResenaListResponse';
import { enviroment } from '../../../enviroments/enviroment';

@Injectable({
  providedIn: 'root',
})
export class ResenaClienteService {
  private baseUrl = `${enviroment.apigateway}/calificacion`;

  constructor(private http: HttpClient) {}

  crearResena(resena: ResenaRequest): Observable<ResultadoResponse<Resena>> {
    return this.http.post<ResultadoResponse<Resena>>(`${this.baseUrl}`, resena);
  }

  listarResenas(): Observable<ResultadoResponse<ResenaListResponse[]>> {
    return this.http.get<ResultadoResponse<ResenaListResponse[]>>(
      `${this.baseUrl}`,
    );
  }

  listarResenasPorUsuario(idUsuario: number): Observable<ResultadoResponse<ResenaListResponse[]>> {
    return this.http.get<ResultadoResponse<ResenaListResponse[]>>(
      `${this.baseUrl}/usuario/${idUsuario}`
    );
  }
  
}
