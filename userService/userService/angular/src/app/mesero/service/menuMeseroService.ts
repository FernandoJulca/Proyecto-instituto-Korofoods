import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResultadoResponse } from '../../shared/dto/ResultadoResponse';
import { enviroment } from '../../../enviroments/enviroment';
import { PlatoDto } from '../../shared/dto/PlatoDto';

@Injectable({
  providedIn: 'root',
})
export class MenuMeseroService {
  private baseUrl = `${enviroment.apigateway}/menu/feign`;

  constructor(private http: HttpClient) {}
  listarPlatos(): Observable<ResultadoResponse<PlatoDto[]>> {
    return this.http.get<ResultadoResponse<PlatoDto[]>>(`${this.baseUrl}`);
  }
}