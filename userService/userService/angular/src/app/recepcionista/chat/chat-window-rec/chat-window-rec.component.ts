import {
  Component,
  OnInit,
  OnDestroy,
  Input,
  ViewChild,
  ElementRef,
  inject,
  OnChanges,
  SimpleChanges,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';

import { HistorialUsuario } from '../../../shared/response/historialUsuarioResponse.model';
import { UserService } from '../../../cliente/service/user.service';
import { WebsocketService } from '../../../cliente/service/websocket.service';
import { MensajeService } from '../../../cliente/service/mensaje.service';
import { Mensaje } from '../../../shared/document/mensaje.model';

@Component({
  selector: 'app-chat-window-rec',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-window-rec.component.html',
  styleUrl: './chat-window-rec.component.css',
})
export class ChatWindowRecComponent implements OnInit, OnDestroy, OnChanges {
  @Input() chat: HistorialUsuario | null = null;
  @ViewChild('messagesContainer') private messagesContainer!: ElementRef;

  private wsService = inject(WebsocketService);
  private mensajeService = inject(MensajeService);
  private userService = inject(UserService);

  mensajes: Mensaje[] = [];
  newMessage: string = '';
  currentUserId: number = 0;
  currentUserName: string = '';
  currentUserAvatar: string = '';

  isLoading: boolean = false;
  isSending: boolean = false;

  clienteNombre: string = '';

  private messageSubscription?: Subscription;

  ngOnInit(): void {
    this.loadCurrentUser();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['chat'] && this.chat) {
      this.loadChatMessages();
      this.subscribeToChat();
      this.clienteNombre = `${this.chat.nombre} ${this.chat.apePaterno}`;
    }
  }

  ngOnDestroy(): void {
    this.messageSubscription?.unsubscribe();
    this.wsService.unsubscribeFromChat();
  }

  private loadCurrentUser(): void {
    const user = this.userService.getUser();
    if (user) {
      this.currentUserId = user.idUsuario || 0;
      this.currentUserName = `${user.nombres} ${user.apePaterno}` || '';
      this.currentUserAvatar = user.imagen || '';
    }
  }

  private loadChatMessages(): void {
    if (!this.chat) return;

    this.isLoading = true;
    this.mensajes = [];

    this.mensajeService.obtenerMensajes(this.chat.chatId, 0, 50).subscribe({
      next: (response) => {
        this.mensajes = response.content;
        this.isLoading = false;

        setTimeout(() => this.scrollToBottom(), 100);
      },
      error: (error) => {
        console.error('Error al cargar mensajes:', error);
        this.isLoading = false;
      },
    });
  }

  private subscribeToChat(): void {
    if (!this.chat) return;

    this.messageSubscription?.unsubscribe();

    this.wsService.subscribeToChat(this.chat.chatId);

    this.messageSubscription = this.wsService.onMessage().subscribe({
      next: (mensaje) => {
        if (mensaje && mensaje.chatId === this.chat?.chatId) {
          if (!this.mensajes.find((m) => m.id === mensaje.id)) {
            this.mensajes.push(mensaje);
            setTimeout(() => this.scrollToBottom(), 50);
          }
        }
      },
    });
  }

  sendMessage(): void {
    if (!this.newMessage.trim() || !this.chat || this.isSending) {
      return;
    }

    if (!this.wsService.connected) {
      alert('No hay conexión con el servidor. Por favor recarga la página.');
      return;
    }

    this.isSending = true;

    this.wsService.sendMessage(
      this.chat.chatId,
      this.newMessage.trim(),
      this.currentUserName,
      this.currentUserAvatar,
      this.currentUserId,
    );

    this.newMessage = '';
    this.isSending = false;
  }

  onKeyPress(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }

  private scrollToBottom(): void {
    try {
      const container = this.messagesContainer?.nativeElement;
      if (container) {
        container.scrollTop = container.scrollHeight;
      }
    } catch (err) {
      console.error('Error al hacer scroll:', err);
    }
  }

  isMyMessage(mensaje: Mensaje): boolean {
    return mensaje.emisorId === this.currentUserId;
  }

  formatMessageTime(fecha: Date): string {
    if (!fecha) return '';

    const date = new Date(fecha);
    return date.toLocaleTimeString('es-PE', {
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  getAvatarUrl(imagen: string): string {
    return imagen || '/img/no-imagen.jpg';
  }

  getClientName(): string {
    return this.clienteNombre;
  }
}
