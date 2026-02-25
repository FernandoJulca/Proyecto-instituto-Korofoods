import {
  Component,
  OnInit,
  ViewChild,
  ElementRef,
  OnDestroy,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ChatbotService } from '../service/chatbot.service'; // Ajusta la ruta según tu estructura
import { Subscription } from 'rxjs';
import { Prompt } from '../../shared/dto/Prompt';


interface Mensaje {
  tipo: 'bot' | 'usuario';
  texto: string;
  hora: string;
}

@Component({
  selector: 'app-support-chat',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './app-support-chat.component.html',
  styleUrl: './app-support-chat.component.css',
})
export class SupportChatComponent implements OnInit, OnDestroy {
  @ViewChild('mensajesContainer') mensajesContainer!: ElementRef;

  // Estado del chat
  chatAbierto: boolean = false;
  mensajesNoLeidos: number = 0;
  tabActivo: string = 'reserva';
  botEscribiendo: boolean = false;

  // Mensajes
  mensajes: Mensaje[] = [];
  mensajeActual: string = '';
  horaActual: string = '';

  // ID de conversación para mantener contexto
  private conversacionId?: string;

  // Subscripción para limpiar
  private chatSubscription?: Subscription;

  // Respuestas predefinidas por categoría
  respuestasPredefinidas: { [key: string]: string } = {
    reserva:
      'Contamos con dos tipos de reserva, simple y especial. ¿De cuál necesitas información?',
    tolerancia:
      'Tenemos un tiempo de tolerancia promedio de 2 horas. Pasado ese tiempo, tu mesa podría ser reasignada. Por favor, avísanos si llegarás tarde enviando un mensaje a recepcionista.',
    pago: 'Después de realizar el pago anticipado de S/ 15.00, podrás revisar el apartado de "Mis Reservas" y solicitar el medio por el cual quieres tu codigo de verificacion para validar tu reserva',
  };

  constructor(private chatbotService: ChatbotService) {}

  ngOnInit(): void {
    this.horaActual = this.obtenerHoraActual();

    // Simular mensaje no leído después de 3 segundos
    setTimeout(() => {
      this.mensajesNoLeidos = 1;
    }, 3000);
  }

  ngOnDestroy(): void {
    // Limpiar subscripciones
    if (this.chatSubscription) {
      this.chatSubscription.unsubscribe();
    }
  }

  toggleChat(): void {
    this.chatAbierto = !this.chatAbierto;

    if (this.chatAbierto) {
      this.mensajesNoLeidos = 0;

      // Hacer scroll al final después de abrir
      setTimeout(() => {
        this.scrollToBottom();
      }, 100);
    }
  }

  cambiarTab(tab: string): void {
    this.tabActivo = tab;

    // Mostrar respuesta predefinida
    this.botEscribiendo = true;

    setTimeout(() => {
      this.botEscribiendo = false;

      const respuesta =
        this.respuestasPredefinidas[tab] || '¿En qué más puedo ayudarte?';

      this.mensajes.push({
        tipo: 'bot',
        texto: respuesta,
        hora: this.obtenerHoraActual(),
      });

      this.scrollToBottom();
    }, 1000);
  }

  enviarMensaje(): void {
    if (!this.mensajeActual.trim() || this.botEscribiendo) {
      return;
    }

    const mensajeUsuario = this.mensajeActual.trim();

    // Agregar mensaje del usuario
    this.mensajes.push({
      tipo: 'usuario',
      texto: mensajeUsuario,
      hora: this.obtenerHoraActual(),
    });

    this.mensajeActual = '';
    this.scrollToBottom();

    // Mostrar indicador de escritura
    this.botEscribiendo = true;

    // Crear el objeto Prompt
    const prompt: Prompt = {
      prompt: mensajeUsuario
    };

    // Llamar al servicio de chatbot
    this.chatSubscription = this.chatbotService.conversacion(prompt).subscribe({
      next: (respuesta: string) => {
        this.botEscribiendo = false;

        // Agregar respuesta del bot
        this.mensajes.push({
          tipo: 'bot',
          texto: respuesta,
          hora: this.obtenerHoraActual(),
        });

        this.scrollToBottom();
      },
      error: (error) => {
        console.error('Error al obtener respuesta del chatbot:', error);
        this.botEscribiendo = false;

        // Mensaje de error amigable
        this.mensajes.push({
          tipo: 'bot',
          texto:
            'Lo siento, estoy teniendo problemas para conectarme. Por favor, intenta de nuevo en un momento.',
          hora: this.obtenerHoraActual(),
        });

        this.scrollToBottom();
      },
    });
  }

  adjuntarArchivo(): void {
    // Implementar lógica para adjuntar archivos
    console.log('Adjuntar archivo');

    this.mensajes.push({
      tipo: 'bot',
      texto:
        'Por favor, selecciona el archivo que deseas adjuntar. Aceptamos imágenes, PDFs y documentos de hasta 10MB.',
      hora: this.obtenerHoraActual(),
    });

    this.scrollToBottom();
  }

  private scrollToBottom(): void {
    setTimeout(() => {
      if (this.mensajesContainer) {
        const elemento = this.mensajesContainer.nativeElement;
        elemento.scrollTop = elemento.scrollHeight;
      }
    }, 100);
  }

  private obtenerHoraActual(): string {
    const ahora = new Date();
    let horas = ahora.getHours();
    const minutos = ahora.getMinutes();
    const ampm = horas >= 12 ? 'PM' : 'AM';

    horas = horas % 12;
    horas = horas ? horas : 12;

    const minutosStr = minutos < 10 ? '0' + minutos : minutos;

    return `${horas}:${minutosStr} ${ampm}`;
  }
}
