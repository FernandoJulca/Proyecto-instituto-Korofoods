import { CommonModule, isPlatformBrowser } from '@angular/common';
import {
  Component,
  Inject,
  PLATFORM_ID,
  ViewEncapsulation,
} from '@angular/core';
import { ResenaListResponse } from '../../shared/dto/ResenaListResponse';
import { ResenaClienteService } from '../service/resenaClienteService';
import { RouterLink } from '@angular/router';
import { SupportChatComponent } from '../app-support-chat/app-support-chat.component';

declare var Swiper: any;

@Component({
  selector: 'app-index',
  imports: [CommonModule, RouterLink, SupportChatComponent],
  templateUrl: './index.component.html',
  styleUrl: './index.component.css',
  encapsulation: ViewEncapsulation.None,
})
export class IndexComponent {
  resenas: ResenaListResponse[] = [];
  isLoading: boolean = true;
  error: string = '';
  resenasSwiper: any;
  imagesSwiper: any;

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private resenaService: ResenaClienteService,
  ) {}

  ngOnInit() {
    this.cargarResenas();
  }

  ngAfterViewInit() {
    if (isPlatformBrowser(this.platformId)) {
      this.initSwiper();
    }
  }

  cargarResenas() {
    this.resenaService.listarResenas().subscribe({
      next: (response) => {
        if (response.valor && response.data) {
          const resenasCinco = response.data.filter(
            (r) => r.calificacion === 5,
          );
          this.resenas = resenasCinco.slice(0, 5);
          this.isLoading = false;

          setTimeout(() => {
            if (isPlatformBrowser(this.platformId)) {
              this.initResenasSwiper();
            }
          }, 100);
        }
      },
      error: (err) => {
        console.error('Error al cargar reseñas:', err);
        this.error = 'No se pudieron cargar las reseñas';
        this.isLoading = false;
      },
    });
  }

  initSwiper() {
    new Swiper('.hero-swiper', {
      loop: true,
      autoplay: {
        delay: 5000,
        disableOnInteraction: false,
      },
      pagination: {
        el: '.swiper-pagination',
        clickable: true,
      },
      navigation: {
        nextEl: '.swiper-button-next',
        prevEl: '.swiper-button-prev',
      },
      effect: 'fade',
      fadeEffect: {
        crossFade: true,
      },
      speed: 1000,
    });
  }

  initResenasSwiper() {
    this.imagesSwiper = new Swiper('.images-swiper', {
      slidesPerView: 1,
      spaceBetween: 0,
      loop: true,
      speed: 800,
      allowTouchMove: false,
    });

    this.resenasSwiper = new Swiper('.resenas-swiper', {
      slidesPerView: 1,
      spaceBetween: 30,
      loop: true,
      autoplay: {
        delay: 6000,
        disableOnInteraction: false,
      },
      pagination: {
        el: '.resenas-pagination',
        clickable: true,
      },
      navigation: {
        nextEl: '.resenas-button-next',
        prevEl: '.resenas-button-prev',
      },
      speed: 800,
      // Sincronizar con el swiper de imágenes
      on: {
        slideChange: (swiper: any) => {
          if (this.imagesSwiper) {
            this.imagesSwiper.slideToLoop(swiper.realIndex);
          }
        },
      },
    });
  }

  getStars(calificacion: number): number[] {
    return Array(5)
      .fill(0)
      .map((_, i) => (i < calificacion ? 1 : 0));
  }

  onImgError(event: any) {
    event.target.src = '/img/banner2.jpg';
  }

  onImgErrorUser(event: any) {
    event.target.src = '/img/user-default.png';
  }
}
