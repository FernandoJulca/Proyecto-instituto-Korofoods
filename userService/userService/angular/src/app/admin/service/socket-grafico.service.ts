import { Injectable, OnDestroy, OnInit } from '@angular/core';
import { enviroment } from '@envs/enviroment';
import { Client, IMessage } from '@stomp/stompjs';
import { BehaviorSubject, Observable } from 'rxjs';

import { filter, debounceTime } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SocketGraficoService implements OnDestroy {
  private client: Client;
  private conectado = new BehaviorSubject<boolean>(false);

  conectado$ = this.conectado.asObservable();

  private graficoUno$ = new BehaviorSubject<any>(null);
  private graficoDos$ = new BehaviorSubject<any>(null);
  private graficoTres$ = new BehaviorSubject<any>(null);
  private graficoCuatro$ = new BehaviorSubject<any>(null);
  private graficoCinco$ = new BehaviorSubject<any>(null);
  private graficoSeis$ = new BehaviorSubject<any>(null);

  constructor() {
    this.client = new Client({
      brokerURL: 'ws://localhost:8098/ws',
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,

      onConnect: () => {
        this.conectado.next(true);
        this.suscribirserAGraficos();
      },

      onDisconnect: () => {
        this.conectado.next(false);
      },

      onStompError: (frame) => {
        console.error('ERROR STOMP:', frame.headers['message']);
        console.error('Detalle del error linea 52: ', frame.body);
        this.conectado.next(false);
      },
    });

    this.client.activate();
  }

  private suscribirserAGraficos(): void {
    this.client.subscribe('/topic/dashboard/grafico-uno', (msg: IMessage) => {
      const response = JSON.parse(msg.body);

      this.graficoUno$.next(response.data);
    });

    this.client.subscribe('/topic/dashboard/grafico-dos', (msg: IMessage) => {
      const response = JSON.parse(msg.body);
      this.graficoDos$.next(response.data);
    });

    this.client.subscribe('/topic/dashboard/grafico-tres', (msg: IMessage) => {
      const response = JSON.parse(msg.body);
      this.graficoTres$.next(response.data);
    });

    this.client.subscribe(
      '/topic/dashboard/grafico-cuatro',
      (msg: IMessage) => {
        const response = JSON.parse(msg.body);
        this.graficoCuatro$.next(response.data);
      },
    );

    this.client.subscribe('/topic/dashboard/grafico-cinco', (msg: IMessage) => {
      const response = JSON.parse(msg.body);
      this.graficoCinco$.next(response.data);
    });

    this.client.subscribe('/topic/dashboard/grafico-seis', (msg: IMessage) => {
      const response = JSON.parse(msg.body);
      this.graficoSeis$.next(response.data);
    });
  }

  inicializarDashboard(mes: number): void {
    if (this.client.connected) {
      this.publicarMes(mes);
    } else {
      const sub = this.conectado$.subscribe((conectado) => {
        if (conectado) {
          this.publicarMes(mes);
          sub.unsubscribe();
        }
      });
    }
  }

  private publicarMes(mes: number) {
    this.client.publish({
      destination: '/app/dashboard/init',
      body: JSON.stringify(mes),
    });
  }
  getGraficoUno$(): Observable<any> {
    return this.graficoUno$.asObservable();
  }
  getGraficoDos$(): Observable<any> {
    return this.graficoDos$.asObservable();
  }
  getGraficoTres$(): Observable<any> {
    return this.graficoTres$.asObservable();
  }
  getGraficoCuatro$(): Observable<any> {
    return this.graficoCuatro$.asObservable();
  }
  getGraficoCinco$(): Observable<any> {
    return this.graficoCinco$.asObservable();
  }
  getGraficoSeis$(): Observable<any> {
    return this.graficoSeis$.asObservable();
  }
  ngOnDestroy(): void {
    this.client.deactivate();
  }
}
