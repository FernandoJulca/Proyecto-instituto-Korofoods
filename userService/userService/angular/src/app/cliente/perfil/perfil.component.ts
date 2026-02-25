import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PerfilClienteResponse } from '../../shared/response/perfilCllienteResponse.model';
import { ResultadoResponse } from '../../shared/dto/ResultadoResponse'; // ajusta ruta
import { PerfilService } from '../service/perfil.service';
import { AuthService } from '../../auth/service/auth.service'; // ajusta ruta
import { RouterLinkWithHref } from '@angular/router';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, RouterLinkWithHref],
  templateUrl: './perfil.component.html',
  styleUrl: './perfil.component.css',
})
export class PerfilComponent implements OnInit {
  perfil: ResultadoResponse<PerfilClienteResponse> | null = null;
  isLoading = false;
  error: string | null = null;

  private idUsuario: number = 0;

  constructor(
    private perfilService: PerfilService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    // Primero obtenemos el usuario desde /auth/me para sacar el idUsuario
    this.authService.getUsuario().subscribe({
      next: (usuario: any) => {
        this.idUsuario = usuario.idUsuario;
        this.cargarPerfil();
      },
      error: (err: any) => {
        console.error('Error al obtener usuario:', err);
        this.error = 'No se pudo identificar al usuario. Inicia sesión nuevamente.';
      },
    });
  }

  cargarPerfil(): void {
    this.isLoading = true;
    this.error = null;

    this.perfilService.getPerfilCliente(this.idUsuario).subscribe({
      next: (data: ResultadoResponse<PerfilClienteResponse>) => {
        this.perfil = data;
        this.isLoading = false;
      },
      error: (err: any) => {
        console.error('Error al cargar perfil:', err);
        this.error = 'No se pudo cargar el perfil. Intenta de nuevo.';
        this.isLoading = false;
      },
    });
  }

  // Accesos directos a los datos del perfil para el HTML
  get datos(): PerfilClienteResponse | null {
    return this.perfil?.data ?? null;
  }

  getInitials(): string {
    const nombres    = this.datos?.nombres    ?? '';
    const apePaterno = this.datos?.apePaterno ?? '';
    return `${nombres.charAt(0)}${apePaterno.charAt(0)}`.toUpperCase() || 'U';
  }
}