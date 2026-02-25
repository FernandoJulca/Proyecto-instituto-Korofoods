import { Injectable, OnDestroy, OnInit } from '@angular/core';
import { PedidoWebSocketMessage } from './detalle-pedido/detalle-pedido.component';
import { Client, StompSubscription } from '@stomp/stompjs';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class WebsocketPedidosService {
  private client: Client | null = null;
  private connected$ = new BehaviorSubject<boolean>(false);
  // Para pedidos
  private pedidoClienteSubject$ =
    new BehaviorSubject<PedidoWebSocketMessage | null>(null);
  private pedidoMeseroSubject$ =
    new BehaviorSubject<PedidoWebSocketMessage | null>(null);
  private pedidoErrorSubject$ = new BehaviorSubject<any>(null);
  private pedidoClienteSubscription: StompSubscription | null = null;
  private pedidoMeseroSubscription: StompSubscription | null = null;
  private pedidoErrorSubscription: StompSubscription | null = null;
  private currentUserId: number | null = null;

  constructor() {}

  //Conectarse al WebSocket
  connect(userId: number, token?: string): void {
    if (this.client && this.client.connected) {
      console.log('🟢 WebSocket Pedidos ya conectado');
      return;
    }
    this.currentUserId = userId;
    this.client = new Client({
      brokerURL: 'ws://localhost:8086/ws',

      connectHeaders: {
        Authorization: token ? `Bearer ${token}` : '',
      },

      debug: (str) => {
        console.log('🍽️ PEDIDOS DEBUG:', str);
      },

      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,

      onConnect: () => {
        console.log('✅ WEBSOCKET PEDIDOS CONECTADO');
        this.connected$.next(true);
      },

      onStompError: (frame) => {
        console.error('❌ ERROR STOMP PEDIDOS:', frame.headers['message']);
        console.error('Detalle:', frame.body);
        this.connected$.next(false);
      },

      onWebSocketClose: () => {
        console.log('🔌 WebSocket Pedidos cerrado');
        this.connected$.next(false);
      },
    });

    this.client.activate();
  }
  disconnect(): void {
    if (this.client) {
      this.unsubscribeFromPedido();
      this.client.deactivate();
      this.connected$.next(false);
      console.log('🔌 WebSocket Pedidos desconectado');
    }
  }

  //SUSCRIBIRSE AL CLIENTE DEL PEDIDO
  subscribeToPedidoCliente(idPedido: number): void {
    if (!this.client || !this.client.connected) {
      console.error('❌ WebSocket no conectado');
      return;
    }

    if (this.pedidoClienteSubscription) {
      this.pedidoClienteSubscription.unsubscribe();
    }

    this.pedidoClienteSubscription = this.client.subscribe(
      `/topic/pedido/${idPedido}/cliente`,
      (message) => {
        const data: PedidoWebSocketMessage = JSON.parse(message.body);
        console.log('📦 Cliente recibe actualización:', data);
        this.pedidoClienteSubject$.next(data);
      },
    );

    // Suscribirse también a errores
    this.subscribeToPedidoError(idPedido);

    console.log(`✅ Suscrito al pedido ${idPedido} (vista cliente)`);
  }

  //SUSCRIBIRSE AL MESERO DEL PEDIDO
  subscribeToPedidoMesero(pedidoId: number): void {
    if (!this.client || !this.client.connected) {
      console.error('❌ WebSocket no conectado');
      return;
    }

    if (this.pedidoMeseroSubscription) {
      this.pedidoMeseroSubscription.unsubscribe();
    }

    this.pedidoMeseroSubscription = this.client.subscribe(
      `/topic/pedido/${pedidoId}/mesero`,
      (message) => {
        const data: PedidoWebSocketMessage = JSON.parse(message.body);
        console.log('👨‍🍳 Mesero recibe actualización:', data);
        this.pedidoMeseroSubject$.next(data);
      },
    );

    // Suscribirse también a errores
    this.subscribeToPedidoError(pedidoId);

    console.log(`✅ Suscrito al pedido ${pedidoId} (vista mesero)`);
  }

  // Suscribirse a errores
  private subscribeToPedidoError(pedidoId: number): void {
    if (this.pedidoErrorSubscription) {
      return; // Ya está suscrito
    }

    this.pedidoErrorSubscription = this.client!.subscribe(
      `/topic/pedido/${pedidoId}/error`,
      (message) => {
        const error = JSON.parse(message.body);
        console.error('❌ Error en pedido:', error);
        this.pedidoErrorSubject$.next(error);
      },
    );
  }

  // Desuscribirse de pedidos
  unsubscribeFromPedido(): void {
    if (this.pedidoClienteSubscription) {
      this.pedidoClienteSubscription.unsubscribe();
      this.pedidoClienteSubscription = null;
    }
    if (this.pedidoMeseroSubscription) {
      this.pedidoMeseroSubscription.unsubscribe();
      this.pedidoMeseroSubscription = null;
    }
    if (this.pedidoErrorSubscription) {
      this.pedidoErrorSubscription.unsubscribe();
      this.pedidoErrorSubscription = null;
    }
    console.log('👋 Desuscrito de pedidos');
  }

  // Agregar plato (WebSocket)
  agregarPlato(idPedido: number, idPlato: number, cantidad: number): void {
    if (!this.client || !this.client.connected) {
      console.error('❌ WebSocket no conectado');
      return;
    }

    const request = {
      idPedido: idPedido,
      idPlato: idPlato,
      cantidad: cantidad,
    };

    this.client.publish({
      destination: `/app/pedido/${idPedido}/agregar`,
      body: JSON.stringify(request),
    });

    console.log('📤 Agregando plato:', request);
  }

  // Entregar plato (WebSocket)
  entregarPlato(pedidoId: number, idDetalle: number): void {
    if (!this.client || !this.client.connected) {
      console.error('❌ WebSocket no conectado');
      return;
    }

    this.client.publish({
      destination: `/app/pedido/${pedidoId}/entregar`,
      body: idDetalle.toString(),
    });

    console.log('📤 Entregando plato:', idDetalle);
  }

  // Cancelar plato (WebSocket)
  cancelarPlato(pedidoId: number, idDetalle: number): void {
    if (!this.client || !this.client.connected) {
      console.error('❌ WebSocket no conectado');
      return;
    }

    this.client.publish({
      destination: `/app/pedido/${pedidoId}/cancelar`,
      body: idDetalle.toString(),
    });

    console.log('📤 Cancelando plato:', idDetalle);
  }

  iniciarPago(idPedido: number): void {
    if (!this.client || !this.client.connected) return;
    this.client.publish({
      destination: `/app/pedido/${idPedido}/pagar`,
      body: JSON.stringify({}),
    });
  }

  elegirMetodo(idPedido: number, metodoPago: string): void {
    if (!this.client || !this.client.connected) return;
    this.client.publish({
      destination: `/app/pedido/${idPedido}/elegirMetodo`,
      body: metodoPago,
    });
  }

  confirmarPago(idPedido: number, metodoPago: string): void {
    if (!this.client || !this.client.connected) return;
    this.client.publish({
      destination: `/app/pedido/${idPedido}/confirmarPago`,
      body: metodoPago,
    });
  }

  onPedidoCliente(): Observable<PedidoWebSocketMessage | null> {
    return this.pedidoClienteSubject$.asObservable();
  }

  onPedidoMesero(): Observable<PedidoWebSocketMessage | null> {
    return this.pedidoMeseroSubject$.asObservable();
  }

  onPedidoError(): Observable<any> {
    return this.pedidoErrorSubject$.asObservable();
  }

  isConnected(): Observable<boolean> {
    return this.connected$.asObservable();
  }

  get connected(): boolean {
    return this.connected$.value;
  }
}
