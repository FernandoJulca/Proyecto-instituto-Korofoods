import { Injectable } from '@angular/core';
import { Client, StompSubscription } from '@stomp/stompjs';
import { BehaviorSubject, Observable } from 'rxjs';
import { Mensaje } from '../../shared/document/mensaje.model';
import { PedidoWebSocketMessage } from '../pedido/detalle-pedido/detalle-pedido.component';

// Interfaces para pedidos

@Injectable({
  providedIn: 'root',
})
export class WebsocketService {
  private client: Client | null = null;
  private connected$ = new BehaviorSubject<boolean>(false);
  private messageSubject$ = new BehaviorSubject<Mensaje | null>(null);
  private notificationSubject$ = new BehaviorSubject<Mensaje | null>(null);

  private chatSubscription: StompSubscription | null = null;
  private userSubscription: StompSubscription | null = null;
  private currentUserId: number | null = null;

  constructor() {}

  //Conectarse al WebSocket
  connect(userId: number, token?: string): void {
    if (this.client && this.client.connected) {
      console.log('WebSocket Conectado');
    }

    this.currentUserId = userId;

    this.client = new Client({
      brokerURL: 'ws://localhost:8098/ws',

      connectHeaders: {
        Authorization: token ? `Bearer ${token}` : '',
      },

      debug: (str) => {
        console.log('STOMP DEBUG: ', str);
      },

      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,

      onConnect: () => {
        console.log('WEBSOCKET CONECTADO CORRECTAMENTE');
        this.connected$.next(true);

        this.subscribeToUserQueue(userId);
      },

      onStompError: (frame) => {
        console.error('ERROR STOMP:', frame.headers['message']);
        console.error('Detalle del error linea 52: ', frame.body);
        this.connected$.next(false);
      },

      onWebSocketClose: () => {
        console.log('WebSocket Cerrando');
        this.connected$.next(false);
      },
    });

    this.client.activate();
  }

  //Desconectarse del WebSocket
  disconnect(): void {
    if (this.client) {
      this.chatSubscription?.unsubscribe();
      this.userSubscription?.unsubscribe();
      this.client.deactivate();
      this.connected$.next(false);
      console.log('WebSocket desconectado');
    }
  }

  //Suscribirse a un chat en especifico osae cuando entra a un chat
  subscribeToChat(chatId: string): void {
    if (!this.client || !this.client.connected) {
      console.error('No se puede contecar WebSocket: no estas conectado');
    }

    if (this.chatSubscription) {
      this.chatSubscription.unsubscribe();
    }

    this.chatSubscription = this.client!.subscribe(
      `/topic/chat/${chatId}`,
      (message) => {
        const receivedMessage: Mensaje = JSON.parse(message.body);
        console.log('Mensaje recibido en el chat:', receivedMessage);
        this.messageSubject$.next(receivedMessage);
      },
    );

    console.log(`Conectado al chat ${chatId}!`);
  }

  unsubscribeFromChat(): void {
    if (this.chatSubscription) {
      this.chatSubscription.unsubscribe();
      this.chatSubscription = null;
      console.log('Saliendo del chat');
    }
  }

  private subscribeToUserQueue(userId: number): void {
    if (!this.client || !this.client.connected) {
      return;
    }

    this.userSubscription = this.client.subscribe(
      `/user/${userId}/queue/messages`,
      (message) => {
        const notification: Mensaje = JSON.parse(message.body);
        console.log('Notificación recibida:', notification);
        this.notificationSubject$.next(notification);
      },
    );

    console.log(`Suscrito a notificaciones del usuario: ${userId}`);
  }

  sendMessage(
    chatId: string,
    content: string,
    sender: string,
    avatar: string,
    emisorId: number,
  ): void {
    if (!this.client || !this.client.connected) {
      console.error('No se puede enviar: WebSocket no conectado');
      return;
    }

    const message = {
      content,
      sender,
      avatar,
      emisorId,
    };

    this.client.publish({
      destination: `/app/chat/${chatId}`,
      body: JSON.stringify(message),
    });

    console.log('Mensaje enviando: ', message);
  }

  // Observables para componentes
  onMessage(): Observable<Mensaje | null> {
    return this.messageSubject$.asObservable();
  }

  onNotification(): Observable<Mensaje | null> {
    return this.notificationSubject$.asObservable();
  }

  isConnected(): Observable<boolean> {
    return this.connected$.asObservable();
  }

  get connected(): boolean {
    return this.connected$.value;
  }
}
