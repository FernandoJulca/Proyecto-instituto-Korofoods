import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SocialAuthResponse } from '../../shared/response/socialAuthResponse.model';
import { Observable } from 'rxjs';

import { enviroment } from '@envs/enviroment';

@Injectable({
  providedIn: 'root',
})
export class GoogleService {
  googleUrl = `${enviroment.apigateway}/auth/google`;
  constructor(private http: HttpClient) {}

  loginWithGoogle(idToken: string): Observable<SocialAuthResponse> {
    return this.http.post<SocialAuthResponse>(this.googleUrl, { idToken });
  }
}
