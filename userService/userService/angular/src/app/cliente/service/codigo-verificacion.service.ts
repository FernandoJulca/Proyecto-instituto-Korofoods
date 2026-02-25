import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { enviroment } from '@envs/enviroment';
import { Observable } from 'rxjs';
import { ResultadoResponse } from '../../shared/dto/ResultadoResponse';
import { EnviarCodigoRequest } from '../../shared/dto/EnviarCodigoRequest';
import { CodigoVerificacionResponse } from '../../shared/dto/CodigoVerificacionResponse';
import { VerificarCodigoRequest } from '../../shared/dto/VerificarCodigoRequest';

@Injectable({
  providedIn: 'root',
})
export class CodigoVerificacionService {
  private baseUrl = `${enviroment.apigateway}/verificacion`;

  constructor(private http: HttpClient) {}

  enviarCodigo(
    request: EnviarCodigoRequest,
  ): Observable<ResultadoResponse<CodigoVerificacionResponse>> {
    return this.http.post<ResultadoResponse<CodigoVerificacionResponse>>(
      `${this.baseUrl}/enviar-codigo`,
      request,
    );
  }

  verificarCodigo(
    request: VerificarCodigoRequest,
  ): Observable<ResultadoResponse<string>> {
    return this.http.post<ResultadoResponse<string>>(
      `${this.baseUrl}/verificar`,
      request,
    );
  }

  reenviarCodigo(
    request: EnviarCodigoRequest,
  ): Observable<ResultadoResponse<CodigoVerificacionResponse>> {
    return this.http.post<ResultadoResponse<CodigoVerificacionResponse>>(
      `${this.baseUrl}/reenviar-codigo`,
      request,
    );
  }
}
