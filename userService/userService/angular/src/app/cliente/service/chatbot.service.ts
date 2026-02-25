import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Prompt } from '../../shared/dto/Prompt';
import { enviroment } from '@envs/enviroment';

@Injectable({
  providedIn: 'root',
})
export class ChatbotService {
  private baseUrl = `${enviroment.apigateway}/chatbot`;

  constructor(private http: HttpClient) {}

  conversacion(prompt: Prompt): Observable<string> {
    return this.http.post(`${this.baseUrl}/conversacion`, prompt, {
      responseType: 'text',
    });
  }
}
