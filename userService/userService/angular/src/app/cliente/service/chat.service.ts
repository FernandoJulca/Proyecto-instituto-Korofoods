import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { enviroment } from '@envs/enviroment';
import { Observable } from 'rxjs';
import { Chat } from '../../shared/document/chat.model';
import { HistorialUsuario } from '../../shared/response/historialUsuarioResponse.model';
import { Recepcionista } from '../../shared/response/recepcionistaResponse.model';

@Injectable({
  providedIn: 'root',
})
export class ChatService {
  private http = inject(HttpClient);
  private apiUrl = `${enviroment.apigateway}/chat`;

  constructor() {}

  //Iniciar chat con el recepcionista
  iniciarChat(emisorId: number, receptorId: number): Observable<Chat> {
    return this.http.post<Chat>(`${this.apiUrl}/start`, {
      emisorId,
      receptorId,
    });
  }

  //Oberner el historial de los chats
  obtenerHistorialChats(idUsuario: number): Observable<HistorialUsuario[]> {
    return this.http.get<HistorialUsuario[]>(
      `${this.apiUrl}/user/${idUsuario}`,
    );
  }

  //Obtener la lista de recepcionistas
  obtenerRecepcionistas(): Observable<Recepcionista[]> {
    return this.http.get<Recepcionista[]>(`${this.apiUrl}/recepcionistas`);
  }
}
