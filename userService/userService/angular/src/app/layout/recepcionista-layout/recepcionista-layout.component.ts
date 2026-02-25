import { Component, inject } from '@angular/core';
import { AuthService } from '../../auth/service/auth.service';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-recepcionista-layout',
  imports: [CommonModule, RouterOutlet],

  templateUrl: './recepcionista-layout.component.html',
  styleUrl: './recepcionista-layout.component.css',
})
export class RecepcionistaLayoutComponent {
  private router = inject(Router);
  private authService = inject(AuthService);

  currentRoute: string = 'home'; // Valor inicial

  navigateToChat(): void {
    this.currentRoute = 'chat';
    this.router.navigate(['/recepcionista/chat']);
  }

  navigateToHome(): void {
    this.currentRoute = 'home';
    this.router.navigate(['/recepcionista/dashboard']);
  }

  navigateToList(): void {
    this.currentRoute = 'list';
    this.router.navigate(['/recepcionista/listado']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
