import { Component } from '@angular/core';
import {
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet,
} from '@angular/router';
import { AuthService } from '../../auth/service/auth.service';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-cliente-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, NgIf],
  templateUrl: './cliente-layout.component.html',
  styleUrl: './cliente-layout.component.css',
})
export class ClienteLayoutComponent {
  isLoggedIn = false;

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.checkLoginStatus();
  }

  checkLoginStatus(): void {
    this.isLoggedIn = this.authService.isLoggedIn();
  }

  navigateToProfile(): void {
    if (this.isLoggedIn) {
      this.router.navigate(['/cliente/perfil']);
    } else {
      this.router.navigate(['/auth/login'], {
        queryParams: { returnUrl: '/cliente/perfil' },
      });
    }
  }

  navigateToReservations(): void {
    if (this.isLoggedIn) {
      this.router.navigate(['/cliente/mis-reservas']);
    } else {
      this.router.navigate(['/auth/login'], {
        queryParams: { returnUrl: '/cliente/mis-reservas' },
      });
    }
  }

  navigateToPedidos(): void {
    if (this.isLoggedIn) {
      this.router.navigate(['/cliente/pedido']);
    } else {
      this.router.navigate(['/auth/login'], {
        queryParams: { returnUrl: '/cliente/pedido' },
      });
    }
  }

  navigateToChats(): void {
    if (this.isLoggedIn) {
      this.router.navigate(['/cliente/chat']);
    } else {
      this.router.navigate(['/auth/login'], {
        queryParams: { returnUrl: '/cliente/perfil' },
      });
    }
  }

  logout(): void {
    this.authService.logout();
    this.isLoggedIn = false;
    this.router.navigate(['/cliente/inicio']);
  }
}
