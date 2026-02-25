import { CommonModule } from '@angular/common';
import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ChatListComponent } from './chat-list/chat-list.component';
import { ChatWindowComponent } from './chat-window/chat-window.component';
import { WebsocketService } from '../service/websocket.service';
import { UserService } from '../service/user.service';
import { AuthService } from '../../auth/service/auth.service';
import { HistorialUsuario } from '../../shared/response/historialUsuarioResponse.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-chat-container',
  standalone: true,
  imports: [CommonModule, ChatListComponent, ChatWindowComponent],
  template: `
    <div class="chat-container">
      <app-chat-list
        (chatSelected)="onChatSelected($event)"
        class="chat-list-section"
      ></app-chat-list>

      <app-chat-window
        [chat]="selectedChat"
        class="chat-window-section"
      ></app-chat-window>
    </div>
  `,
  styles: [
    `
      .chat-container {
        display: grid;
        grid-template-columns: 380px 1fr;
        height: 100dvh;
        overflow: hidden;
      }

      .chat-list-section {
        display: flex;
        flex-direction: column;
      }

      .chat-window-section {
        display: flex;
        flex-direction: column;
      }

      @media (max-width: 1024px) {
        .chat-container {
          grid-template-columns: 320px 1fr;
        }
      }

      @media (max-width: 768px) {
        .chat-container {
          display: flex;
          flex-direction: column;
          height: 100dvh;
        }

        .chat-list-section,
        .chat-window-section {
          flex: 1;
          height: 100%;
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
export class ChatContainerComponent implements OnInit, OnDestroy {
  private wsService = inject(WebsocketService);
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private router = inject(Router);

  selectedChat: HistorialUsuario | null = null;
  isUserLoaded: boolean = false;

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
        queryParams: { returnUrl: '/cliente/chat' },
      });
      return;
    }

    console.log('Token encontrado');

    let user = this.userService.getUser();

    if (user && user.idUsuario) {
      this.isUserLoaded = true;
      this.initializeWebSocket(user.idUsuario, token);
    } else {
      this.authService.getUsuario().subscribe({
        next: (usuario) => {
          // Guardar usuario en memoria y localStorage
          this.userService.setUser(usuario);

          this.isUserLoaded = true;
          this.initializeWebSocket(usuario.idUsuario, token);
        },
        error: (error) => {
          console.error('Error al obtener usuario:', error);

          this.authService.logout();
          this.router.navigate(['/auth/login'], {
            queryParams: { returnUrl: '/cliente/chat' },
          });
        },
      });
    }
  }

  private initializeWebSocket(userId: number, token: string): void {
    console.log('Conectando WebSocket para usuario:', userId);
    this.wsService.connect(userId, token);
  }

  onChatSelected(chat: HistorialUsuario): void {
    this.selectedChat = chat;
    console.log('Chat seleccionado:', chat);
  }
}
