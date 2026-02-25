import { CommonModule } from '@angular/common';
import {
  Component,
  ElementRef,
  inject,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HistorialUsuario } from '../../../shared/response/historialUsuarioResponse.model';
import { WebsocketService } from '../../service/websocket.service';
import { UserService } from '../../service/user.service';
import { MensajeService } from '../../service/mensaje.service';
import { Mensaje } from '../../../shared/document/mensaje.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-chat-window',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-window.component.html',
  styleUrl: './chat-window.component.css',
})
export class ChatWindowComponent implements OnInit, OnDestroy, OnChanges {
  @Input() chat: HistorialUsuario | null = null;
  @ViewChild('messagesContainer') private messagesContainer!: ElementRef;

  private wsService = inject(WebsocketService);
  private userService = inject(UserService);
  private mensajeService = inject(MensajeService);

  mensajes: Mensaje[] = [];
  newMessage: string = '';
  currentUserId: number = 0;
  currentUserName: string = '';
  currentUserAvatar: string = '';

  isLoading: boolean = false;
  isSending: boolean = false;

  private messageSubscription?: Subscription;

  ngOnInit(): void {
    this.loadCurrentUser();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['chat'] && this.chat) {
      this.loadChatMessages();
      this.subscribeToChat();
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
      this.currentUserName = user.nombres || '';
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
        console.log('Mensajes cargados:', this.mensajes);
        this.isLoading = false;

        // Scroll al final después de cargar mensajes
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

    // Desuscribirse del chat anterior
    this.messageSubscription?.unsubscribe();

    // Suscribirse al nuevo chat en el WebSocket
    this.wsService.subscribeToChat(this.chat.chatId);

    // Escuchar nuevos mensajes
    this.messageSubscription = this.wsService.onMessage().subscribe({
      next: (mensaje) => {
        if (mensaje && mensaje.chatId === this.chat?.chatId) {
          console.log('Nuevo mensaje recibido:', mensaje);

          // Agregar el mensaje si no existe ya (evitar duplicados)
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

  getContactName(): string {
    if (!this.chat) return '';
    return `${this.chat.nombre} ${this.chat.apePaterno}`;
  }
}
