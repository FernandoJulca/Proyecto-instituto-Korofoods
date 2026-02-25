import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, Observable, tap } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { jwtDecode } from 'jwt-decode';

// Entornos para el despliegue de docker
import { enviroment } from '@envs/enviroment';
import { ResultadoResponseSinEntidad } from '../../shared/response/resultadoResponse.models';
import { Usuario } from '../../shared/model/usuario.model';
import { RegistroSocialRequest } from '../../shared/request/registroSocialRequest.model';
import { ResultadoResponse } from '../../shared/dto/ResultadoResponse';
import { SocialRegisterData } from '../../shared/dto/socialRegisterData.model';
import { UserService } from '../../cliente/service/user.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = `${enviroment.apigateway}/auth/login`;
  private registerUrl = `${enviroment.apigateway}/auth/register`;
  private userUrl = `${enviroment.apigateway}/auth/me`;
  private completarRegistroUrl = `${enviroment.apigateway}/auth/social/register`;

  constructor(
    private http: HttpClient,
    private userService: UserService,
  ) {
    console.log(' API URL:', this.apiUrl);
  }

  login(
    credentials: { correo: string; clave: string } | Usuario,
  ): Observable<any> {
    return this.http
      .post<{ token: string }>(this.apiUrl, credentials, {
        headers: { 'Content-Type': 'application/json' },
      })
      .pipe(
        tap((response) => {
          console.log('Login exitoso, respuesta:', response);
        }),
        catchError((error) => {
          console.error(' Error en login:', error);
          throw error;
        }),
      );
  }

  register(usuario: Usuario): Observable<ResultadoResponseSinEntidad> {
    console.log('Usuario a registrar:', usuario);

    const formData = new FormData();

    formData.append('nombres', usuario.nombres);
    formData.append('apePaterno', usuario.apePaterno);
    formData.append('apeMaterno', usuario.apeMaterno);
    formData.append('correo', usuario.correo);
    formData.append('clave', usuario.clave);
    formData.append('tipoDoc', usuario.tipoDoc);
    formData.append('nroDoc', usuario.nroDoc);
    formData.append('direccion', usuario.direccion);
    formData.append('telefono', usuario.telefono);
    formData.append(
      'distrito.idDistrito',
      usuario.distrito.idDistrito.toString(),
    );

    if (usuario.imagenMultipart) {
      formData.append('imagenMultipart', usuario.imagenMultipart);
    }

    return this.http
      .post<ResultadoResponseSinEntidad>(this.registerUrl, formData)
      .pipe(
        tap((response) => {
          console.log('Registro exitoso, respuesta:', response);
        }),
        catchError((error) => {
          console.error('Error en registro:', error);
          throw error;
        }),
      );
  }

  getUsuario(): Observable<any> {
    const token = this.getToken();

    if (!token) {
      console.error('No hay token disponible');
      throw new Error('No token found');
    }

    return this.http
      .get(this.userUrl, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .pipe(
        tap((response) => {
          console.log('Usuario obtenido:', response);
        }),
        catchError((error) => {
          console.error('Error al obtener usuario:', error);
          throw error;
        }),
      );
  }

  saveToken(token: string): void {
    localStorage.setItem('auth_token', token);
    console.log('Token guardado en localStorage');
  }

  getToken(): string | null {
    const token = localStorage.getItem('auth_token');
    return token;
  }

  getRolesFromToken(): string[] {
    const token = this.getToken();

    if (!token) {
      return [];
    }

    try {
      const decoded: any = jwtDecode(token);
      console.log('Token decodificado:', decoded);
      const roles = decoded.roles || [];
      return roles;
    } catch (error) {
      console.error('Error al decodificar token:', error);
      return [];
    }
  }

  isLoggedIn(): boolean {
    const loggedIn = this.getToken() !== null;
    return loggedIn;
  }

  logout(): void {
    localStorage.removeItem('auth_token');
    this.userService.clearUser();
  }

  completarRegistroSocial(
    request: RegistroSocialRequest,
  ): Observable<ResultadoResponse<SocialRegisterData>> {
    console.log('Completando registro social:', request);

    return this.http
      .post<ResultadoResponse<SocialRegisterData>>(
        this.completarRegistroUrl,
        request,
        {
          headers: { 'Content-Type': 'application/json' },
        },
      )
      .pipe(
        tap((response) => {
          console.log('Registro social completado:', response);
        }),
        catchError((error) => {
          console.error('Error completando registro:', error);
          throw error;
        }),
      );
  }
}
