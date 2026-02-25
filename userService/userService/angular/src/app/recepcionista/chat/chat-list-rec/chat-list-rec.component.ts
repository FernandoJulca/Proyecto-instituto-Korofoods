import {
  Component,
  OnInit,
  OnDestroy,
  inject,
  Output,
  EventEmitter,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';

import { HistorialUsuario } from '../../../shared/response/historialUsuarioResponse.model';
import { UserService } from '../../../cliente/service/user.service';
import { ChatService } from '../../../cliente/service/chat.service';
import { WebsocketService } from '../../../cliente/service/websocket.service';

@Component({
  selector: 'app-chat-list-rec',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-list-rec.component.html',
  styleUrl: './chat-list-rec.component.css',
})
export class ChatListRecComponent implements OnInit, OnDestroy {
  @Output() chatSelected = new EventEmitter<HistorialUsuario>();

  private chatService = inject(ChatService);
  private wsService = inject(WebsocketService);
  private userService = inject(UserService);

  chats: HistorialUsuario[] = [];
  filteredChats: HistorialUsuario[] = [];

  searchQuery: string = '';
  currentUserId: number = 0;
  selectedChatId: string | null = null;

  private notificationSubscription?: Subscription;

  ngOnInit(): void {
    this.loadCurrentUser();
    this.loadChats();
    this.subscribeToNotifications();
  }

  ngOnDestroy(): void {
    this.notificationSubscription?.unsubscribe();
  }

  private loadCurrentUser(): void {
    const user = this.userService.getUser();
    if (user) {
      this.currentUserId = user.idUsuario || 0;
    }
  }

  private loadChats(): void {
    if (!this.currentUserId) return;

    this.chatService.obtenerHistorialChats(this.currentUserId).subscribe({
      next: (chats) => {
        this.chats = chats;
        this.filteredChats = chats;
        console.log('Chats del recepcionista cargados:', chats);
      },
      error: (error) => {
        console.error('Error al cargar chats:', error);
      },
    });
  }

  public refreshChats(): void {
    console.log('Refrescando lista de chats...');
    this.loadChats();
  }

  private subscribeToNotifications(): void {
    this.notificationSubscription = this.wsService.onNotification().subscribe({
      next: (mensaje) => {
        if (mensaje) {
          console.log('Nueva notificación recibida:', mensaje);
          this.updateChatInList(mensaje.chatId, mensaje.contenido);
        }
      },
    });
  }

  private updateChatInList(chatId: string, ultimoMensaje: string): void {
    const chat = this.chats.find((c) => c.chatId === chatId);
    if (chat) {
      chat.ultimoMensaje = ultimoMensaje;
      chat.fechaUltimoMensaje = new Date();

      // Mover al principio
      this.chats = [chat, ...this.chats.filter((c) => c.chatId !== chatId)];
      this.filteredChats = [...this.chats];
    } else {
      // Chat nuevo, recargar lista
      this.loadChats();
    }
  }

  onSearchChange(): void {
    const query = this.searchQuery.toLowerCase().trim();

    if (!query) {
      this.filteredChats = [...this.chats];
      return;
    }

    this.filteredChats = this.chats.filter((chat) => {
      const nombre = `${chat.nombre} ${chat.apePaterno}`.toLowerCase();
      const mensaje = chat.ultimoMensaje.toLowerCase();
      return nombre.includes(query) || mensaje.includes(query);
    });
  }

  selectChat(chat: HistorialUsuario): void {
    this.selectedChatId = chat.chatId;
    this.chatSelected.emit(chat);
  }

  formatTime(date: Date): string {
    if (!date) return '';

    const messageDate = new Date(date);
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);

    if (messageDate.toDateString() === today.toDateString()) {
      return messageDate.toLocaleTimeString('es-PE', {
        hour: '2-digit',
        minute: '2-digit',
      });
    }

    if (messageDate.toDateString() === yesterday.toDateString()) {
      return 'Ayer';
    }

    return messageDate.toLocaleDateString('es-PE', {
      day: '2-digit',
      month: 'short',
    });
  }

  truncateMessage(message: string, maxLength: number = 40): string {
    if (!message) return '';
    return message.length > maxLength
      ? message.substring(0, maxLength) + '...'
      : message;
  }

  getAvatarUrl(imagen: string): string {
    return imagen || '/img/no-imagen.jpg';
  }

  getUnreadCount(chatId: string): number {
    return 0;
  }
}
