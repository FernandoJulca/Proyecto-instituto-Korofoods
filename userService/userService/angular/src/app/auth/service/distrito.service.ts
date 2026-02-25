import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { enviroment } from '@envs/enviroment';
import { Distrito } from '../../shared/model/distrito.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class DistritoService {
  private url = `${enviroment.apigateway}/distrito`;
  constructor(private http: HttpClient) {}

  listarDistritos(): Observable<Distrito[]> {
    return this.http.get<Distrito[]>(`${this.url}/list`);
  }
}
