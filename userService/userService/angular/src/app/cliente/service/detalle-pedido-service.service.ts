import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { enviroment } from '@envs/enviroment';
import { Observable } from 'rxjs';
import { ResultadoResponse } from '../../shared/dto/ResultadoResponse';
import { DetallePedidoResponse } from '../../shared/response/detallePedidoResponse';
import { Pedido } from '../../shared/model/pedido.model';
import { DetallePedidoMeseroResponse } from '../../shared/response/detallePedidoMeseroResponse';
import { DetallePedidoUsuarioResponse } from '../../shared/response/detallePedidoUsuarioResponse';

@Injectable({
  providedIn: 'root',
})
export class DetallePedidoServiceService {
  private baseUrl = `${enviroment.apigateway}/pedido`;

  constructor(private http: HttpClient) {}

  obtenerDetallePorPedido(
    pedidoId: number,
  ): Observable<ResultadoResponse<DetallePedidoResponse[]>> {
    return this.http.get<ResultadoResponse<DetallePedidoResponse[]>>(
      `${this.baseUrl}/list/${pedidoId}`,
    );
  }

  obtenerPedidosDelCliente(
    idUsuario: number,
  ): Observable<ResultadoResponse<Pedido[]>> {
    return this.http.get<ResultadoResponse<Pedido[]>>(
      `${this.baseUrl}/${idUsuario}/list`,
    );
  }

  obtnerMesero(
    idPedido: number,
  ): Observable<ResultadoResponse<DetallePedidoMeseroResponse>> {
    return this.http.get<ResultadoResponse<DetallePedidoMeseroResponse>>(
      `${this.baseUrl}/mesero/${idPedido}`,
    );
  }

  obtenerCliente(
    idPedido: number,
  ): Observable<ResultadoResponse<DetallePedidoUsuarioResponse>> {
    return this.http.get<ResultadoResponse<DetallePedidoUsuarioResponse>>(
      `${this.baseUrl}/cliente/${idPedido}`,
    );
  }

  cambiarEstado(idPedidio: number): Observable<ResultadoResponse<Pedido>> {
    return this.http.put<ResultadoResponse<Pedido>>(
      `${this.baseUrl}/estadoPagado?idPedido=${idPedidio}`,
      null,
    );
  }

  obtenerPedido(idPedido: number): Observable<ResultadoResponse<Pedido>> {
    return this.http.get<ResultadoResponse<Pedido>>(
      `${this.baseUrl}/obtener/${idPedido}`,
    );
  }
}
