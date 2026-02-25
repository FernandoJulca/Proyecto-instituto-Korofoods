import iziToast from 'izitoast';
import 'izitoast/dist/css/iziToast.min.css';

export class AlertIziToast {
  static success(title: string, message: string = '¡Éxito!') {
    iziToast.success({
      title,
      message,
      position: 'topRight',
      timeout: 4000,
      progressBar: true,
      animateInside: true,
      transitionIn: 'fadeInDown',
      transitionOut: 'fadeOutUp',
      icon: 'fa fa-check-circle',
      color: '#4CAF50',
    });
  }

  static info(title: string, message: string = 'Info') {
    iziToast.info({
      title,
      message,
      position: 'topRight',
      timeout: 4000,
      progressBar: true,
      animateInside: true,
      transitionIn: 'fadeInDown',
      transitionOut: 'fadeOutUp',
      icon: 'fa fa-info-circle',
      color: '#2196F3',
    });
  }

  static warning(title: string, message: string = '¡Cuidado!') {
    iziToast.warning({
      title,
      message,
      position: 'topRight',
      timeout: 4000,
      progressBar: true,
      animateInside: true,
      transitionIn: 'fadeInDown',
      transitionOut: 'fadeOutUp',
      icon: 'fa fa-exclamation-triangle',
      color: '#FFC107',
    });
  }

  static error(title: string, message: string) {
    iziToast.error({
      title,
      message,
      position: 'topRight',
      timeout: 5000,
      progressBar: true,
      animateInside: true,
      transitionIn: 'fadeInDown',
      transitionOut: 'fadeOutUp',
      icon: 'fa fa-times-circle',
      color: '#F44336',
    });
  }
}
