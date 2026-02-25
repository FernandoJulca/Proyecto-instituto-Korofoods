import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ResenaListResponse } from '../../shared/dto/ResenaListResponse';
import { ResenaClienteService } from '../service/resenaClienteService';
import { AuthService } from '../../auth/service/auth.service';

@Component({
  selector: 'app-resena-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './resena.component.html',
  styleUrl: './resena.component.css',
})
export class ResenaComponent implements OnInit {
  resenas: ResenaListResponse[] = [];
  resenasFiltradas: ResenaListResponse[] = [];
  isLoading = true;
  error = '';

  // Paginación
  currentPage = 1;
  itemsPerPage = 9;

  // Filtros
  verSoloMias = false;
  idUsuarioActual: number | null = null;
  filtroCalificacion: number | null = null;

  // Autenticación
  isLoggedIn: boolean = false;
  isLoadingUser: boolean = true;

  constructor(
    private resenaService: ResenaClienteService,
    private authService: AuthService,
    private router: Router,
  ) {}

  ngOnInit() {
    this.verificarAutenticacion();
  }

  // Verificar si el usuario está autenticado
  verificarAutenticacion() {
    const token = this.authService.getToken();
    
    if (!token) {
      this.isLoggedIn = false;
      this.isLoadingUser = false;
      this.cargarResenas(); // Cargar todas las reseñas para visitantes
      console.log('Usuario no autenticado - Mostrando todas las reseñas');
      return;
    }

    this.authService.getUsuario().subscribe({
      next: (response) => {
        console.log('Usuario autenticado:', response);
        this.isLoggedIn = true;
        this.idUsuarioActual = response.idUsuario || response.id;
        this.isLoadingUser = false;
        this.cargarResenas(); // Cargar reseñas con funcionalidad completa
      },
      error: (error) => {
        console.error('Error al verificar autenticación:', error);
        this.isLoggedIn = false;
        this.isLoadingUser = false;
        localStorage.removeItem('auth_token');
        this.cargarResenas(); // Cargar todas las reseñas como visitante
      },
    });
  }

  cargarResenas() {
    this.isLoading = true;
    this.error = '';

    this.resenaService.listarResenas().subscribe({
      next: (response) => {
        if (response.valor && response.data) {
          this.resenas = response.data;
          this.aplicarFiltros();
          this.isLoading = false;
        }
      },
      error: (err) => {
        console.error('Error al cargar reseñas:', err);
        this.error = 'No se pudieron cargar las reseñas';
        this.isLoading = false;
      },
    });
  }

  cargarMisResenas() {
    if (!this.isLoggedIn || !this.idUsuarioActual) {
      this.redirectToLogin();
      return;
    }

    this.isLoading = true;
    this.error = '';

    this.resenaService.listarResenasPorUsuario(this.idUsuarioActual).subscribe({
      next: (response) => {
        if (response.valor && response.data) {
          this.resenas = response.data;
          this.aplicarFiltros();
          this.isLoading = false;
        }
      },
      error: (err) => {
        console.error('Error al cargar mis reseñas:', err);
        this.error = 'No se pudieron cargar tus reseñas';
        this.isLoading = false;
      },
    });
  }

  toggleMisResenas() {
    if (!this.isLoggedIn) {
      this.redirectToLogin();
      return;
    }

    this.verSoloMias = !this.verSoloMias;
    this.currentPage = 1;

    if (this.verSoloMias) {
      this.cargarMisResenas();
    } else {
      this.cargarResenas();
    }
  }

  filtrarPorCalificacion(calificacion: number | null) {
    this.filtroCalificacion = calificacion;
    this.currentPage = 1;
    this.aplicarFiltros();
  }

  aplicarFiltros() {
    this.resenasFiltradas = this.resenas;

    // Filtrar por calificación si está activo
    if (this.filtroCalificacion !== null) {
      this.resenasFiltradas = this.resenasFiltradas.filter(
        (resena) => resena.calificacion === this.filtroCalificacion,
      );
    }
  }

  // Paginación
  get resenasPaginadas(): ResenaListResponse[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    return this.resenasFiltradas.slice(startIndex, endIndex);
  }

  get totalPages(): number {
    return Math.ceil(this.resenasFiltradas.length / this.itemsPerPage);
  }

  get pages(): number[] {
    const maxPagesToShow = 5;
    const pages: number[] = [];

    if (this.totalPages <= maxPagesToShow) {
      return Array.from({ length: this.totalPages }, (_, i) => i + 1);
    }

    const startPage = Math.max(1, this.currentPage - 2);
    const endPage = Math.min(this.totalPages, startPage + maxPagesToShow - 1);

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }

    return pages;
  }

  goToPage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  previousPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  nextPage() {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  // Navegación
  irACrearResena() {
    if (!this.isLoggedIn) {
      this.redirectToLogin();
      return;
    }
    this.router.navigate(['/cliente/crear-resenia']);
  }

  redirectToLogin() {
    this.router.navigate(['/login'], {
      queryParams: { returnUrl: '/cliente/resenia' }
    });
  }

  // Helpers
  getStars(calificacion: number): number[] {
    return Array(5)
      .fill(0)
      .map((_, i) => (i < calificacion ? 1 : 0));
  }

  getCalificacionPromedio(): number {
    if (this.resenasFiltradas.length === 0) return 0;
    const suma = this.resenasFiltradas.reduce(
      (acc, r) => acc + r.calificacion,
      0,
    );
    return suma / this.resenasFiltradas.length;
  }

  getTotalPorCalificacion(calificacion: number): number {
    return this.resenas.filter((r) => r.calificacion === calificacion).length;
  }

  onImgError(event: any) {
    event.target.src = '/img/user-default.png';
  }

  get maxItem(): number {
    return Math.min(this.currentPage * this.itemsPerPage, this.resenasFiltradas.length);
  }

  onImgErrorEntidad(event: any) {
    event.target.src = '/img/no-imagen.jpg';
  }

  // Verificar si una reseña es del usuario actual
  esMiResena(resena: ResenaListResponse): boolean {
    return this.isLoggedIn && resena.idUsuario === this.idUsuarioActual;
  }
}