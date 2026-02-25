export interface PlatoDto {
  idPlato: number;
  nombre: string;
  tipoPlato: string;
  imagen: string;
  precio:number;
  etiquetas: EtiquetaDTO[];
}


export interface EtiquetaDTO{
  idEtiqueta:number;
  nombre: string;
}