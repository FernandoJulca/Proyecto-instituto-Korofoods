import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  NavigationEnd,
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet,
} from '@angular/router';
import { filter } from 'rxjs';
import { AuthService } from '../../auth/service/auth.service';

@Component({
  selector: 'app-mesero-layout',
  imports: [RouterOutlet, CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './mesero-layout.component.html',
  styleUrl: './mesero-layout.component.css',
})
export class MeseroLayoutComponent {
  sidebarCollapsed = false;
  currentTime = new Date();
  userName = '';

  pageTitles: { [key: string]: string } = {
    '/mesero/ordenes': 'Órdenes',
    '/mesero/nueva-orden': 'Nueva Orden',
    '/dashboard/mesas': 'Mesas',
    '/dashboard/menu': 'Menú',
    '/dashboard/reservas': 'Reservas',
  };

  currentRoute = '';

  constructor(
    private router: Router,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    // reloj
    setInterval(() => {
      this.currentTime = new Date();
    }, 60000);

    // cambiar título según ruta
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        this.currentRoute = event.url;
      });

    // Evitar error de localStorage en SSR
    if (typeof window === 'undefined') {
      console.warn('SSR detectado — no se ejecutará getUsuario()');
      this.userName = 'Usuario';
      return;
    }

    // Si no hay token, evita error
    const token = this.authService.getToken();
    if (!token) {
      this.userName = 'Usuario';
      return;
    }

    // obtener usuario
    this.authService.getUsuario().subscribe({
  next: (u: any) => {
    console.log('usuario data', u); 
    if (u) {
      this.userName = `${u.nombres} ${u.apePaterno}`;
      console.log('usuario es:', this.userName);
    } else {
      this.userName = 'Usuario';
    }
  },
  error: (err) => {
    console.error('Error obteniendo usuario:', err);
    this.userName = 'Usuario';
  },
});

  }

  toggleSidebar(): void {
    this.sidebarCollapsed = !this.sidebarCollapsed;
  }

  getUserInitials(): string {
    return this.userName
      .split(' ')
      .map((n) => n[0])
      .join('')
      .toUpperCase();
  }

  getPageTitle(): string {
    return this.pageTitles[this.currentRoute] || 'Órdenes';
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/cliente/inicio']);
  }
}
