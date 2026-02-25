import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { enviroment } from '../../../enviroments/enviroment';
import { ResultadoResponse } from '../../shared/dto/ResultadoResponse';
import { PedidoResumenDto } from '../../shared/dto/PedidoResumenDto';
import { PedidoRequestoDto } from '../../shared/dto/PedidoRequestDto';
import { Pedido } from '../../shared/model/pedido.model';
import { DetallePedidoPagar } from '../../shared/response/detallePedidoPagar.model';
import { PlatoMasVendidoDto } from '../../shared/dto/PlatoMasVendidoDto';
import { VentasPorFechaMesaDto } from '../../shared/dto/VentasPorFechaMesaDto';

@Injectable({
  providedIn: 'root',
})
export class PedidoMeseroService {
  private baseUrl = `${enviroment.apigateway}/pedido`;

  constructor(private http: HttpClient) {}

  listarPedidos(
    estado?: string,
  ): Observable<ResultadoResponse<PedidoResumenDto[]>> {
    let url = `${this.baseUrl}`;
    if (estado) {
      url += `?estado=${estado}`;
    }
    return this.http.get<ResultadoResponse<PedidoResumenDto[]>>(url);
  }

  crearPedido(dto: PedidoRequestoDto): Observable<ResultadoResponse<Pedido>> {
    return this.http.post<ResultadoResponse<Pedido>>(`${this.baseUrl}`, dto);
  }

  pagarResponse(
    idPedido: number,
  ): Observable<ResultadoResponse<DetallePedidoPagar>> {
    return this.http.get<ResultadoResponse<DetallePedidoPagar>>(
      `${this.baseUrl}/procederPago?idPedido=${idPedido}`,
    );
  }

  reporteVentasPorMesa(
  fechaInicio?: string,
  fechaFin?: string,
  idMesa?: number
): Observable<ResultadoResponse<VentasPorFechaMesaDto[]>> {
  let params = new HttpParams();
  if (fechaInicio) params = params.set('fechaInicio', fechaInicio);
  if (fechaFin)    params = params.set('fechaFin', fechaFin);
  if (idMesa)      params = params.set('idMesa', idMesa.toString());
  return this.http.get<ResultadoResponse<VentasPorFechaMesaDto[]>>(
    `${this.baseUrl}/reporte/ventas-por-mesa`, { params }
  );
}

// HU23
reportePlatosMasVendidos(
  fechaInicio?: string,
  fechaFin?: string
): Observable<ResultadoResponse<PlatoMasVendidoDto[]>> {
  let params = new HttpParams();
  if (fechaInicio) params = params.set('fechaInicio', fechaInicio);
  if (fechaFin)    params = params.set('fechaFin', fechaFin);
  return this.http.get<ResultadoResponse<PlatoMasVendidoDto[]>>(
    `${this.baseUrl}/reporte/platos-mas-vendidos`, { params }
  );
}
}
