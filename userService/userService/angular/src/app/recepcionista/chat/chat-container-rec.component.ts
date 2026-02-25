import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ChatListRecComponent } from './chat-list-rec/chat-list-rec.component';
import { CommonModule } from '@angular/common';
import { ChatWindowRecComponent } from './chat-window-rec/chat-window-rec.component';
import { WebsocketService } from '../../cliente/service/websocket.service';
import { UserService } from '../../cliente/service/user.service';
import { AuthService } from '../../auth/service/auth.service';
import { Router } from '@angular/router';
import { HistorialUsuario } from '../../shared/response/historialUsuarioResponse.model';

@Component({
  selector: 'app-chat-container-rec',
  standalone: true,
  imports: [CommonModule, ChatListRecComponent, ChatWindowRecComponent],
  template: `
    <!-- Loading State -->
    <div class="loading-container" *ngIf="!isUserLoaded">
      <div class="spinner"></div>
      <p>Cargando panel de mensajería...</p>
    </div>

    <!-- Chat Container -->
    <div class="chat-container" *ngIf="isUserLoaded">
      <app-chat-list-rec
        (chatSelected)="onChatSelected($event)"
        class="chat-list-section"
      ></app-chat-list-rec>

      <app-chat-window-rec
        [chat]="selectedChat"
        class="chat-window-section"
      ></app-chat-window-rec>
    </div>
  `,
  styles: [
    `
      .chat-container {
        display: grid;
        grid-template-columns: 380px 1fr;
        height: 100vh;
        overflow: hidden;
        background: #f8f9fa;
      }

      .chat-list-section {
        display: flex;
        flex-direction: column;
        height: 100vh;
        overflow: hidden;
      }

      .chat-window-section {
        display: flex;
        flex-direction: column;
        height: 100vh;
        overflow: hidden;
      }

      .loading-container {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        height: 100vh;
        background: #f8f9fa;
      }

      .spinner {
        width: 50px;
        height: 50px;
        border: 4px solid #e5e7eb;
        border-top-color: #667eea;
        border-radius: 50%;
        animation: spin 0.8s linear infinite;
        margin-bottom: 1rem;
      }

      @keyframes spin {
        to {
          transform: rotate(360deg);
        }
      }

      .loading-container p {
        color: #6b7280;
        font-size: 1.1rem;
      }

      @media (max-width: 1024px) {
        .chat-container {
          grid-template-columns: 320px 1fr;
        }
      }

      @media (max-width: 768px) {
        .chat-container {
          grid-template-columns: 1fr;
        }

        .chat-list-section {
          display: none;
        }

        .chat-container.show-list .chat-list-section {
          display: flex;
        }

        .chat-container.show-list .chat-window-section {
          display: none;
        }
      }
    `,
  ],
})
export class ChatContainerRecComponent implements OnInit, OnDestroy {
  private wsService = inject(WebsocketService);
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private router = inject(Router);

  selectedChat: HistorialUsuario | null = null;
  isUserLoaded: boolean = false;

  constructor() {}

  ngOnInit(): void {
    this.initializeUser();
  }

  ngOnDestroy(): void {
    this.wsService.disconnect();
  }

  private initializeUser(): void {
    const token = this.authService.getToken();

    if (!token) {
      console.error('No hay token, redirigiendo al login');
      this.router.navigate(['/auth/login'], {
        queryParams: { returnUrl: '/recepcionista/chat' },
      });
      return;
    }

    console.log('Token encontrado');

    let user = this.userService.getUser();

    if (user && user.idUsuario) {
      console.log('Usuario recepcionista cargado:', user);
      this.isUserLoaded = true;
      this.initializeWebSocket(user.idUsuario, token);
    } else {
      console.log('Obteniendo usuario del backend...');
      this.authService.getUsuario().subscribe({
        next: (usuario) => {
          console.log('Usuario recepcionista obtenido:', usuario);
          this.userService.setUser(usuario);
          this.isUserLoaded = true;
          this.initializeWebSocket(usuario.idUsuario, token);
        },
        error: (error) => {
          console.error('Error al obtener usuario:', error);
          this.authService.logout();
          this.router.navigate(['/auth/login'], {
            queryParams: { returnUrl: '/recepcionista/chat' },
          });
        },
      });
    }
  }

  private initializeWebSocket(userId: number, token: string): void {
    console.log('Conectando WebSocket para recepcionista:', userId);
    this.wsService.connect(userId, token);
  }

  onChatSelected(chat: HistorialUsuario): void {
    this.selectedChat = chat;
    console.log('Chat seleccionado:', chat);
  }
}
