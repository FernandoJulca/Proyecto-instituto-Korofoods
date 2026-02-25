import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Usuario } from '../../shared/model/usuario.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private currentUserSubject: BehaviorSubject<Usuario | null>;
  public currentUser$: Observable<Usuario | null>;

  constructor() {
    const storedUser = this.loadUserFromStorage();
    this.currentUserSubject = new BehaviorSubject<Usuario | null>(storedUser);
    this.currentUser$ = this.currentUserSubject.asObservable();
  }

  getUser(): Usuario | null {
    return this.currentUserSubject.value;
  }

  setUser(usuario: Usuario): void {
    this.currentUserSubject.next(usuario);
    this.saveUserToStorage(usuario);
    console.info('Usuario guardardo');
  }

  clearUser(): void {
    this.currentUserSubject.next(null);
    this.removeUserFromStorage();
    console.log('Usuario eliminado');
  }

  isLoggedIn(): boolean {
    return this.currentUserSubject.value !== null;
  }

  getRol(): string {
    const user = this.currentUserSubject.value;
    return user?.rol?.descripcion || '';
  }

  getCurrentUser(): Observable<Usuario | null> {
    return this.currentUser$;
  }

  private saveUserToStorage(user: Usuario): void {
    try {
      localStorage.setItem('current_user', JSON.stringify(user));
    } catch (error) {
      console.error('Error al guardar usuario en localStorage:', error);
    }
  }

  private loadUserFromStorage(): Usuario | null {
    try {
      const userData = localStorage.getItem('current_user');
      if (userData) {
        return JSON.parse(userData);
      }
    } catch (error) {
      console.error('Error al cargar usuario desde localStorage:', error);
    }
    return null;
  }

  private removeUserFromStorage(): void {
    try {
      localStorage.removeItem('current_user');
    } catch (error) {
      console.error('Error al eliminar usuario de localStorage:', error);
    }
  }
}
