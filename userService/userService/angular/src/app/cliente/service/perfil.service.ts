import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { enviroment } from '@envs/enviroment';
import { PerfilClienteResponse } from '../../shared/response/perfilCllienteResponse.model';
import { Observable } from 'rxjs';
import { ResultadoResponse } from '../../shared/dto/ResultadoResponse';

@Injectable({
  providedIn: 'root',
})
export class PerfilService {
  private baseUrl = `${enviroment.apigateway}/auth`;
  constructor(private http: HttpClient) {}

  getPerfilCliente(idUsuario: number): Observable<ResultadoResponse<PerfilClienteResponse>> {
    return this.http.get<ResultadoResponse<PerfilClienteResponse>>(`${this.baseUrl}/${idUsuario}`);
  }
}
