import { NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';

interface ContactInfo {
  icon: string;
  title: string;
  info: string[];
  link?: string;
}

interface SocialMedia {
  name: string;
  icon: string;
  url: string;
  color: string;
}
@Component({
  selector: 'app-contacto',
  imports: [NgIf, NgFor, RouterLink],
  templateUrl: './contacto.component.html',
  styleUrl: './contacto.component.css'
})
export class ContactoComponent implements OnInit {
  
  contactInfo: ContactInfo[] = [
    {
      icon: 'location',
      title: 'Ubicación',
      info: [
        'Av. Principal 123',
        'Miraflores, Lima',
        'Perú'
      ],
      link: 'https://maps.google.com/?q=KoroFood+Restaurant'
    },
    {
      icon: 'phone',
      title: 'Teléfono',
      info: [
        '+51 987 654 321',
        '+51 912 345 678'
      ],
      link: 'tel:+51987654321'
    },
    {
      icon: 'email',
      title: 'Email',
      info: [
        'info@korofood.com',
        'reservas@korofood.com'
      ],
      link: 'mailto:info@korofood.com'
    },
    {
      icon: 'clock',
      title: 'Horario',
      info: [
        'Lun - Jue: 12:00 PM - 11:00 PM',
        'Vie - Sáb: 12:00 PM - 12:00 AM',
        'Domingo: 12:00 PM - 10:00 PM'
      ]
    }
  ];

  socialMedia: SocialMedia[] = [
    {
      name: 'Facebook',
      icon: 'facebook',
      url: 'https://facebook.com/korofood',
      color: '#1877f2'
    },
    {
      name: 'Instagram',
      icon: 'instagram',
      url: 'https://instagram.com/korofood',
      color: '#e4405f'
    },
    {
      name: 'WhatsApp',
      icon: 'whatsapp',
      url: 'https://wa.me/51987654321',
      color: '#25d366'
    },
    {
      name: 'TikTok',
      icon: 'tiktok',
      url: 'https://tiktok.com/@korofood',
      color: '#000000'
    }
  ];

  constructor() { }

  ngOnInit(): void { }

  getIcon(iconName: string): string {
    const icons: { [key: string]: string } = {
      location: 'M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z',
      phone: 'M6.62 10.79c1.44 2.83 3.76 5.14 6.59 6.59l2.2-2.2c.27-.27.67-.36 1.02-.24 1.12.37 2.33.57 3.57.57.55 0 1 .45 1 1V20c0 .55-.45 1-1 1-9.39 0-17-7.61-17-17 0-.55.45-1 1-1h3.5c.55 0 1 .45 1 1 0 1.25.2 2.45.57 3.57.11.35.03.74-.25 1.02l-2.2 2.2z',
      email: 'M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z',
      clock: 'M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z'
    };
    return icons[iconName] || '';
  }
}
