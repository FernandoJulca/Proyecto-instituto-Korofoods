import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Zona } from '../../shared/enums/Zona';
import { Observable } from 'rxjs';
import { ResultadoResponse } from '../../shared/dto/ResultadoResponse';
import { enviroment } from '../../../enviroments/enviroment';
import { MesaDto } from '../../shared/dto/MesaDto';



@Injectable({
  providedIn: 'root',
})
export class MesasServiceService {

  private baseUrl = `${enviroment.apigateway}/mesa/feign`;
    
  constructor(private http: HttpClient) {}

  obtenerMesasPorZona(
    zona: Zona,
    cantidadPersonas?: number,
  ): Observable<ResultadoResponse<MesaDto[]>> {
    let params: any = {};

    if (cantidadPersonas) {
      params.cantidadPersonas = cantidadPersonas;
    }

    return this.http.get<ResultadoResponse<MesaDto[]>>(
      `${this.baseUrl}/zona/${zona}`,
      { params },
    );
  }
}
