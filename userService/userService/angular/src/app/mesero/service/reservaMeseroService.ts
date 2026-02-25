import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResultadoResponse } from '../../shared/dto/ResultadoResponse';
import { enviroment } from '../../../enviroments/enviroment';
import { ReservaDto } from '../../shared/dto/ReservaDto';

@Injectable({
  providedIn: 'root',
})
export class ReservaMeseroService {
  private baseUrl = `${enviroment.apigateway}/reserva/feign`;

  constructor(private http: HttpClient) {}

  getReservationById(codigo: String): Observable<ResultadoResponse<ReservaDto>> {
    return this.http.get<ResultadoResponse<ReservaDto>>(
      `${this.baseUrl}/${codigo}`,
    );
  }
}
