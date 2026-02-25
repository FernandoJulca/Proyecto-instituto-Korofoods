import { Component, HostListener, OnInit } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { PlatoDto } from '../../shared/dto/PlatoDto';
import { MenuClienteService } from '../service/menuClienteService';
declare var Swiper: any;

interface PlatosPorTipo {
  [key: string]: PlatoDto[];
}

@Component({
  selector: 'app-menu',
  imports: [NgIf, NgFor],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css',
})
export class MenuComponent implements OnInit {
  isDownloading = false;
  platos: PlatoDto[] = [];
  platosFiltrados: PlatosPorTipo = {};
  filtroActivo: string = 'Todos';
  isLoading = false;

  tipoPlatoLabels: any = {
    E: 'Entradas',
    S: 'Platos Principales',
    P: 'Postres',
    B: 'Bebidas',
  };

  tiposPlato: string[] = ['Todos', 'E', 'S', 'P', 'B'];

  showScrollButton = false;

  @HostListener('window:scroll', [])
  onWindowScroll() {
    this.showScrollButton = window.pageYOffset > 300;
  }

  scrollToTop(): void {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  constructor(private menuService: MenuClienteService) {}

  ngOnInit(): void {
    this.cargarPlatos();
  }

  ngAfterViewInit(): void {}

  cargarPlatos(): void {
    this.isLoading = true;
    this.menuService.listarPlatos().subscribe({
      next: (response) => {
        if (response.valor && response.data) {
          this.platos = response.data;
          this.organizarPlatosPorTipo();
          setTimeout(() => {
            this.inicializarSwipers();
          }, 100);
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error al cargar platos:', error);
        this.isLoading = false;
      },
    });
  }

  organizarPlatosPorTipo(): void {
    this.platosFiltrados = {};

    this.platos.forEach((plato) => {
      if (!this.platosFiltrados[plato.tipoPlato]) {
        this.platosFiltrados[plato.tipoPlato] = [];
      }
      this.platosFiltrados[plato.tipoPlato].push(plato);
    });
  }

  filtrarPlatos(tipo: string): void {
    this.filtroActivo = tipo;
    setTimeout(() => {
      this.inicializarSwipers();
    }, 100);
  }

  obtenerTiposVisibles(): string[] {
    if (this.filtroActivo === 'Todos') {
      return Object.keys(this.platosFiltrados);
    } else {
      return [this.filtroActivo];
    }
  }

  inicializarSwipers(): void {
    const tipos = this.obtenerTiposVisibles();

    tipos.forEach((tipo) => {
      const swiperElement = document.querySelector(
        `.swiper-${this.getSwiperClass(tipo)}`,
      );
      if (swiperElement && this.platosFiltrados[tipo]?.length > 0) {
        new Swiper(swiperElement, {
          slidesPerView: 1,
          spaceBetween: 20,
          navigation: {
            nextEl: `.swiper-button-next-${this.getSwiperClass(tipo)}`,
            prevEl: `.swiper-button-prev-${this.getSwiperClass(tipo)}`,
          },
          pagination: {
            el: `.swiper-pagination-${this.getSwiperClass(tipo)}`,
            clickable: true,
          },
          breakpoints: {
            640: {
              slidesPerView: 2,
              spaceBetween: 20,
            },
            1024: {
              slidesPerView: 3,
              spaceBetween: 30,
            },
          },
          loop: this.platosFiltrados[tipo].length > 3,
        });
      }
    });
  }

  getSwiperClass(tipo: string): string {
    return tipo.toLowerCase().replace(/\s+/g, '-');
  }

  descargarMenuPdf(): void {
    this.isDownloading = true;

    this.menuService.descargarMenuPdf().subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `KoroFood-Menu-${new Date().getTime()}.pdf`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
        this.isDownloading = false;
      },
      error: (error) => {
        console.error('Error al descargar el PDF:', error);
        this.isDownloading = false;
      },
    });
  }
  setDefaultImage(event: any) {
    event.target.src = '/img/no-imagen.jpg';
  }
  getTipoPlato(tipo: string): string {
    switch (tipo) {
      case 'E':
        return 'ENTRADA';
      case 'S':
        return 'SEGUNDO';
      case 'P':
        return 'POSTRE';
      case 'B':
        return 'BEBIDA';
      default:
        return tipo;
    }
  }
}
