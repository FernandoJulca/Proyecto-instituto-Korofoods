import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormsModule,
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { AlertIziToast } from '../../util/iziToastAlert.service';
import {
  GoogleSigninButtonModule,
  SocialAuthService,
  SocialUser,
} from '@abacritt/angularx-social-login';

import { RegisterComponent } from '../register/register.component';

//servicios a consumir
import { DistritoService } from '../service/distrito.service';
import { AuthService } from '../service/auth.service';
import { GithubService } from '../service/github.service';
import { GoogleService } from '../service/google.service';

//modelos a utilizar

import { Distrito } from '../../shared/model/distrito.model';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../../cliente/service/user.service';
import { Usuario } from '../../shared/model/usuario.model';

import { enviroment } from '@envs/enviroment';
import { TipoDocumento } from '../../shared/enums/tipoDocumento.enum';
import { Subscription } from 'rxjs';

//Request a utilizar
import { RegistroSocialRequest } from '../../shared/request/registroSocialRequest.model';

//DtoS a utulizar
import { GoogleDto } from '../../shared/dto/googleDto.model';
import { GitHubDto } from '../../shared/dto/githubDto.model';
import { SocialUserDataDto } from '../../shared/dto/socialUserDataDto.model';

//Response a utulizar
import { SocialAuthResponse } from '../../shared/response/socialAuthResponse.model';
import { ResultadoResponse } from '../../shared/dto/ResultadoResponse'; //Con entidad
import { ResultadoResponseSinEntidad } from '../../shared/response/resultadoResponse.models'; //sin entidad
import { SocialRegisterData } from '../../shared/dto/socialRegisterData.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    GoogleSigninButtonModule,
    RegisterComponent,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent implements OnInit {
  //Formularios tanto como Login y registro

  registerForm!: FormGroup; //para el formulario de registro
  loginForm!: FormGroup; // para el formulario de login
  sociaLoginRegisterForm!: FormGroup;

  distritos: Distrito[] = [];
  errorMessage: string = '';
  successMessage: string = '';
  isLoginMode: boolean = true;

  showSocialRegisterModal: boolean = false;

  showPassword: boolean = false;
  showConfirmPassword: boolean = false;
  showLoginPassword: boolean = false;

  tempSocialData: SocialUserDataDto | null = null;
  tempToken: string | null = null;

  //Inyeccion d servicios
  private authStateSubscription?: Subscription;
  private distritoService = inject(DistritoService);
  private socialAuth = inject(AuthService); // incluye el login/registro
  private gitHubService = inject(GithubService); //login/register github
  private googleService = inject(GoogleService); //login/register google
  private authService = inject(SocialAuthService); // servicio para autenticación con Google
  private userService = inject(UserService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private formBuilder = inject(FormBuilder);

  constructor() {
    this.initForms();
  }

  ngOnInit(): void {
    this.cargarDistritos();
    this.setupGoogleAuth();
    this.setupGithubAuth();
  }

  ngOnDestroy(): void {
    if (this.authStateSubscription) {
      this.authStateSubscription.unsubscribe();
    }
  }

  private initForms(): void {
    //Formulario del registro
    this.registerForm = this.formBuilder.group({
      nombres: [
        '',
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(50),
        ],
      ],
      apePaterno: [
        '',
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(50),
        ],
      ],
      apeMaterno: [
        '',
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(50),
        ],
      ],
      correo: [
        '',
        [Validators.required, Validators.email, Validators.maxLength(50)],
      ],
      clave: [
        '',
        [
          Validators.required,
          Validators.minLength(6),
          Validators.maxLength(225),
        ],
      ],
      tipoDoc: ['', Validators.required],
      nroDoc: [
        '',
        [
          Validators.required,
          Validators.pattern(/^[0-9]{8}$/),
          Validators.minLength(2),
          Validators.maxLength(8),
        ],
      ],
      direccion: ['', [Validators.required, Validators.maxLength(50)]],
      idDistrito: ['', [Validators.required]],
      telefono: [
        '',
        [
          Validators.required,
          Validators.pattern(/^[0-9]{9}$/),
          Validators.minLength(2),
          Validators.maxLength(9),
        ],
      ],
    });

    //Formulario del Login
    this.loginForm = this.formBuilder.group({
      correo: ['', [Validators.required, Validators.email]],
      clave: ['', Validators.required],
    });

    // Formulario para completar registro social
    this.sociaLoginRegisterForm = this.formBuilder.group({
      apePaterno: [
        '',
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(50),
        ],
      ],
      apeMaterno: [
        '',
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(50),
        ],
      ],
      tipoDoc: ['', Validators.required],
      nroDoc: ['', [Validators.required, Validators.pattern(/^[0-9]{8}$/)]],
      direccion: ['', [Validators.required, Validators.maxLength(50)]],
      idDistrito: ['', Validators.required],
      telefono: ['', [Validators.required, Validators.pattern(/^[0-9]{9}$/)]],
      clave: [
        '',
        [
          Validators.required,
          Validators.minLength(6),
          Validators.maxLength(225),
        ],
      ],
    });
  }

  cargarDistritos(): void {
    this.distritoService.listarDistritos().subscribe({
      next: (data: Distrito[]) => {
        this.distritos = data;
        console.log('Distritos cargados:', this.distritos);
      },
      error: (error) => {
        console.error('Error al cargar distritos:', error);
      },
    });
  }

  //--------------------GOOGLE LOGIN / REGISTER -------------------------------

  private setupGoogleAuth(): void {
    try {
      if (!this.authService) {
        console.error('SocialAuthService no disponible');
        return;
      }

      this.authStateSubscription = this.authService.authState.subscribe({
        next: (socialUser: SocialUser | null) => {
          if (socialUser) {
            console.log('Usuario Google detectado:', socialUser);

            const idToken = (socialUser as any).idToken;

            if (!idToken) {
              console.error('No se pudo obtener idToken de Google');
              AlertIziToast.error('Error', 'No se pudo autenticar con Google');
              return;
            }

            this.handleGoogleLogin(idToken);
          }
        },
        error: (err) => {
          console.error('Error en authState:', err);
        },
      });
    } catch (error) {
      console.error('Error al configurar Google Auth:', error);
    }
  }

  private handleGoogleLogin(idToken: string): void {
    this.googleService.loginWithGoogle(idToken).subscribe({
      next: (response: SocialAuthResponse) => {
        console.log('Google login:', response);
        this.procesarRespuestaSocial(response);
      },
      error: (err) => {
        console.error('Error en Google login:', err);
        AlertIziToast.error('Error', 'No se pudo autenticar con Google');
      },
    });
  }

  onGoogleAuth(): void {
    console.log('Google authentication initiated');

    alert('Autenticación con Google en proceso...');
  }

  //--------------------GITHUB LOGIN / REGISTER -------------------------------

  private setupGithubAuth(): void {
    const code = this.route.snapshot.queryParamMap.get('code');

    if (code) {
      console.log('Código GitHub detectado:', code);
      this.handleGithubLogin(code);
    }
  }

  private handleGithubLogin(code: string): void {
    this.gitHubService.loginWithGithub(code).subscribe({
      next: (response: SocialAuthResponse) => {
        console.log('Respuesta de GitHub login:', response);
        this.procesarRespuestaSocial(response);
      },
      error: (err) => {
        console.error('Error en GitHub login:', err);
        AlertIziToast.error('Error', 'No se pudo autenticar con GitHub');
        this.router.navigate(['/auth/login']);
      },
    });
  }

  onGithubAuth() {
    console.log('GitHub authentication initiated');
    const { githubClientId, githubRedirectUri } = enviroment;

    window.location.href =
      `https://github.com/login/oauth/authorize` +
      `?client_id=${githubClientId}` +
      `&scope=user:email` +
      `&redirect_uri=${githubRedirectUri}`;
  }

  private procesarRespuestaSocial(response: SocialAuthResponse): void {
    if (response.usuarioExistente) {
      console.log('Usuario existente, iniciando sesión...');
      this.socialAuth.saveToken(response.token!);
      this.completarLoginYRedirigir();
    } else {
      console.log('Usuario nuevo, mostrar formulario...');
      this.tempSocialData = response.socialUserDataDto;
      this.tempToken = response.tempToken;
      this.showSocialRegisterModal = true;
    }
  }

  handleCompletarRegistro(request: RegistroSocialRequest): void {
    this.socialAuth.completarRegistroSocial(request).subscribe({
      next: (resultado: ResultadoResponse<SocialRegisterData>) => {
        if (resultado.valor) {
          AlertIziToast.success('¡Registro exitoso!', 'Bienvenido a KoroFoods');

          const data: any = resultado.data;
          this.socialAuth.saveToken(data.token);

          this.showSocialRegisterModal = false;
          this.tempSocialData = null;
          this.tempToken = null;

          this.completarLoginYRedirigir();
        } else {
          AlertIziToast.error(
            'Error',
            resultado.mensaje || 'No se pudo completar el registro',
          );
        }
      },
      error: (error) => {
        AlertIziToast.error(
          'Error',
          error.error?.mensaje || 'Error al completar el registro',
        );
      },
    });
  }

  cerrarModalSocial(): void {
    this.showSocialRegisterModal = false;
    this.tempSocialData = null;
    this.tempToken = null;
  }

  // ========== COMPLETAR LOGIN Y REDIRIGIR ==========
  private completarLoginYRedirigir(): void {
    this.socialAuth.getUsuario().subscribe({
      next: (usuario) => {
        this.userService.setUser(usuario);
        const descripcion = usuario.rol.descripcion;

        AlertIziToast.success('¡Bienvenido!', `Hola ${usuario.nombres}`);

        //MODIFICAR LAS RUTAS DESPUES DEL LOGIN A DONDE SE VAN A REDIRIGIR SEGUN SUS ENDOPOINTS
        switch (descripcion) {
          case 'A':
            this.router.navigate(['/admin/dashboard']);
            break;
          case 'C':
            this.router.navigate(['/cliente/inicio']);
            break;
          case 'R':
            this.router.navigate(['/recepcionista']);
            break;
          case 'M':
            this.router.navigate(['/mesero/ordenes']);
            break;
          default:
            this.router.navigate(['/auth/login']);
        }
      },
      error: (error) => {
        console.error('Error al obtener usuario:', error);
        AlertIziToast.error('Error', 'No se pudo obtener datos del usuario');
      },
    });
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPassword(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  toggleLoginPassword(): void {
    this.showLoginPassword = !this.showLoginPassword;
  }

  switchToLogin(): void {
    this.isLoginMode = true;
  }

  switchToSignup(): void {
    this.isLoginMode = false;
  }

  //LOGIN CON CORREO Y CONTRASEÑA
  onLogin(): void {
    console.log('Formulario login válido?', this.loginForm.valid);
    console.log('Datos enviados:', this.loginForm.value);

    if (this.loginForm.invalid) {
      AlertIziToast.error('Error', 'Por favor completa todos los campos');
      return;
    }

    this.socialAuth.login(this.loginForm.value).subscribe({
      next: (response: any) => {
        this.socialAuth.saveToken(response.token);
        this.completarLoginYRedirigir();
      },
      error: (error) => {
        console.error('Error LOGIN:', error);
        AlertIziToast.error('Error', 'Correo o contraseña incorrectos');
      },
    });
  }

  //REGISTRO DE UNA CUENTA NUEVA
  onSignUp(): void {
    if (this.registerForm.invalid) {
      this.markFormGroupTouched(this.registerForm);
      AlertIziToast.error('Error', 'Completar todos los campos correctamente');
      return;
    }

    const formValue = this.registerForm.value;
    const usuario: Usuario = {
      nombres: formValue.nombres,
      apePaterno: formValue.apePaterno,
      apeMaterno: formValue.apeMaterno,
      correo: formValue.correo,
      clave: formValue.clave,
      tipoDoc: formValue.tipoDoc,
      nroDoc: formValue.nroDoc,
      direccion: formValue.direccion,
      distrito: {
        idDistrito: parseInt(formValue.idDistrito),
      },
      telefono: formValue.telefono,
    };

    this.socialAuth.register(usuario).subscribe({
      next: (resultado: ResultadoResponseSinEntidad) => {
        if (resultado.valor) {
          AlertIziToast.success(
            '¡Registro Exitoso!',
            'Por favor, inicia sesión',
          );
          this.registerForm.reset();
          setTimeout(() => {
            this.isLoginMode = true;
          }, 1000);
        } else {
          AlertIziToast.error(
            'Error',
            resultado.mensaje || 'No se pudo registrar',
          );
        }
      },
      error: (error) => {
        console.error('Error en registro:', error);
        AlertIziToast.error(
          'Error',
          error.error?.mensaje || 'Error al registrar',
        );
      },
    });
  }

  verificarLoggin(): Boolean {
    return this.socialAuth.isLoggedIn();
  }

  //Implmentar metodo para recuperar contraseña
  onForgotPassword(): void {
    const email = prompt(
      'Por favor ingresa tu email para recuperar tu contraseña:',
    );

    if (email) {
      console.log('Password reset requested for:', email);

      alert('Se ha enviado un correo de recuperación a ' + email);
    }
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach((key) => {
      const control = formGroup.get(key);
      control?.markAsTouched();

      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.registerForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getErrorMessage(fieldName: string): string {
    const field = this.registerForm.get(fieldName);

    if (field?.hasError('required')) {
      return 'Este campo es requerido';
    }
    if (field?.hasError('minlength')) {
      return `Mínimo ${field.errors?.['minlength'].requiredLength} caracteres`;
    }
    if (field?.hasError('maxlength')) {
      return `Máximo ${field.errors?.['maxlength'].requiredLength} caracteres`;
    }
    if (field?.hasError('pattern')) {
      if (fieldName === 'nroDoc') {
        return 'Debe tener 8 dígitos';
      }
      if (fieldName === 'telefono') {
        return 'Debe tener 9 dígitos';
      }
    }
    return '';
  }
}
