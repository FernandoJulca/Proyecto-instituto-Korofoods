import { SocialUserDataDto } from '../dto/socialUserDataDto.model';

export interface SocialAuthResponse {
  usuarioExistente: boolean;
  token: string | null;
  tempToken: string | null;
  socialUserDataDto: SocialUserDataDto | null;
}
