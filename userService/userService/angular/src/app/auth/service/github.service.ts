import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { enviroment } from '@envs/enviroment';
import { SocialAuthResponse } from '../../shared/response/socialAuthResponse.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class GithubService {
  private githubUrl = `${enviroment.apigateway}/auth/github`;

  constructor(private httpClient: HttpClient) {}

  loginWithGithub(code: string): Observable<SocialAuthResponse> {
    return this.httpClient.post<SocialAuthResponse>(
      `${this.githubUrl}?code=${code}`,
      {},
    );
  }
}
