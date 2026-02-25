import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import {
  Chart,
  DoughnutController,
  ArcElement,
  Legend,
  Tooltip,
  Title,
  BarController,
  BarElement,
  LineController,
  LineElement,
  PointElement,
  LinearScale,
  CategoryScale,
} from 'chart.js';

Chart.register(
  DoughnutController,
  ArcElement,
  BarController,
  BarElement,
  LineController,
  LineElement,
  PointElement,
  LinearScale,
  CategoryScale,
  Tooltip,
  Legend,
  Title,
);

bootstrapApplication(AppComponent, appConfig)
  .then(() => {
    console.log('Aplicación iniciada correctamente');
  })
  .catch((err) => {
    console.error('Error al iniciar aplicación:', err);
    console.error('Stack trace:', err.stack);
  });
