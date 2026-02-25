import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { enviroment } from '@envs/enviroment';
import { Observable } from 'rxjs';
import { Mensaje } from '../../shared/document/mensaje.model';

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class MensajeService {
  private http = inject(HttpClient);
  private apiUrl = `${enviroment.apigateway}/chat`;
  constructor() {}

  obtenerMensajes(
    chatId: string,
    page: number = 0,
    size: number = 20,
  ): Observable<PageResponse<Mensaje>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<Mensaje>>(
      `${this.apiUrl}/${chatId}/mensajes`,
      { params },
    );
  }
}
