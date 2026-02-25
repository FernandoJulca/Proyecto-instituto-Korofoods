export interface ResultadoResponse<T> {
  valor: boolean;       
  mensaje?: string;
  data: T;             
}