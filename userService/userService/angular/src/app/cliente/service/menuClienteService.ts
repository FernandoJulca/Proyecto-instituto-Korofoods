import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResultadoResponse } from '../../shared/dto/ResultadoResponse';
import { enviroment } from '../../../enviroments/enviroment';
import { PlatoDto } from '../../shared/dto/PlatoDto';

@Injectable({
  providedIn: 'root',
})
export class MenuClienteService {
  private baseUrl = `${enviroment.apigateway}/menu/feign`;
  private baseUrlService = `${enviroment.apigateway}/menu`;

  constructor(private http: HttpClient) {}
  listarPlatos(): Observable<ResultadoResponse<PlatoDto[]>> {
    return this.http.get<ResultadoResponse<PlatoDto[]>>(`${this.baseUrl}`);
  }

  descargarMenuPdf(): Observable<Blob> {
    return this.http.get(`${this.baseUrlService}/pdf`, {
      responseType: 'blob',
    });
  }

}
