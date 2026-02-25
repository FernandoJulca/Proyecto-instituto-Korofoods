// src/app/services/usuario-soap.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { enviroment } from '@envs/enviroment';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  RespuestaSoap,
  UsuarioSoap,
} from '../../shared/model/usuarioSoap,model';

@Injectable({ providedIn: 'root' })
export class UsuarioSoapService {
  private url = `${enviroment.apigateway}/soap`;
  private readonly NAMESPACE = 'http://koroFoods.com/usuario';

  private headers = new HttpHeaders({
    'Content-Type': 'text/xml;charset=UTF-8',
    SOAPAction: '',
  });

  constructor(private http: HttpClient) {}

  private send(xml: string): Observable<string> {
    return this.http.post(this.url, xml, {
      headers: this.headers,
      responseType: 'text',
    });
  }

  private parseXmlResponse(xmlString: string, parser: DOMParser): Document {
    return parser.parseFromString(xmlString, 'text/xml');
  }

  private getTextContent(doc: Document, tagName: string): string {
    const element = doc.getElementsByTagName(tagName)[0];
    return element ? element.textContent || '' : '';
  }

  crearUsuario(
    data: UsuarioSoap,
  ): Observable<RespuestaSoap & { idUsuario?: number }> {
    const xml = `
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:usu="http://koroFoods.com/usuario">
 <soapenv:Header/>
 <soapenv:Body>
  <usu:crearUsuarioRequest>
    <usu:nombres>${data.nombres}</usu:nombres>
    <usu:apePaterno>${data.apePaterno}</usu:apePaterno>
    <usu:apeMaterno>${data.apeMaterno}</usu:apeMaterno>
    <usu:correo>${data.correo}</usu:correo>
    <usu:clave>${data.clave}</usu:clave>
    <usu:tipoDoc>${data.tipoDoc}</usu:tipoDoc>
    <usu:nroDoc>${data.nroDoc}</usu:nroDoc>
    ${data.direccion ? `<usu:direccion>${data.direccion}</usu:direccion>` : ''}
    ${data.telefono ? `<usu:telefono>${data.telefono}</usu:telefono>` : ''}
    ${data.idDistrito ? `<usu:idDistrito>${data.idDistrito}</usu:idDistrito>` : ''}
    <usu:idRol>${data.idRol}</usu:idRol>
  </usu:crearUsuarioRequest>
 </soapenv:Body>
</soapenv:Envelope>`;

    return this.send(xml).pipe(
      map((response) => {
        const parser = new DOMParser();
        const doc = this.parseXmlResponse(response, parser);

        const exitosoNode = Array.from(doc.getElementsByTagName('*')).find(
          (n) => n.localName === 'exitoso',
        );
        const mensajeNode = Array.from(doc.getElementsByTagName('*')).find(
          (n) => n.localName === 'mensaje',
        );
        const codigoNode = Array.from(doc.getElementsByTagName('*')).find(
          (n) => n.localName === 'codigo',
        );
        const idUsuarioNode = Array.from(doc.getElementsByTagName('*')).find(
          (n) => n.localName === 'idUsuario',
        );

        return {
          exitoso: exitosoNode?.textContent === 'true',
          mensaje: mensajeNode?.textContent || '',
          codigo: codigoNode?.textContent || undefined,
          idUsuario: idUsuarioNode
            ? Number(idUsuarioNode.textContent)
            : undefined,
        };
      }),
    );
  }

  private getElementTextNS(parent: Element, tagName: string): string {
    const element = parent.getElementsByTagNameNS(this.NAMESPACE, tagName)[0];
    return element ? (element.textContent?.trim() ?? '') : '';
  }

  private parseIntOrUndefined(value: string): number | undefined {
    const parsed = parseInt(value);
    return isNaN(parsed) ? undefined : parsed;
  }

  listarUsuarios(
    idRol?: number | null,
    activo?: boolean | null,
  ): Observable<UsuarioSoap[]> {
    const filtros = [
      idRol != null ? `<usu:idRol>${idRol}</usu:idRol>` : '',
      activo != null ? `<usu:activo>${activo}</usu:activo>` : '',
    ].join('');

    const requestBody = filtros
      ? `<usu:listarUsuariosRequest>${filtros}</usu:listarUsuariosRequest>`
      : `<usu:listarUsuariosRequest/>`;

    const xml = `
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:usu="http://koroFoods.com/usuario">
   <soapenv:Header/>
   <soapenv:Body>
      ${requestBody}
   </soapenv:Body>
</soapenv:Envelope>`.trim();

    return this.send(xml).pipe(
      map((response) => {
        const parser = new DOMParser();
        const doc = parser.parseFromString(response, 'text/xml');

        const usuariosElements = doc.getElementsByTagNameNS(
          this.NAMESPACE,
          'usuarios',
        );

        const usuarios: UsuarioSoap[] = [];

        for (let i = 0; i < usuariosElements.length; i++) {
          const usuario = usuariosElements[i];

          usuarios.push({
            idUsuario:
              this.parseIntOrUndefined(
                this.getElementTextNS(usuario, 'idUsuario'),
              ) ?? 0,
            nombres: this.getElementTextNS(usuario, 'nombres'),
            apePaterno: this.getElementTextNS(usuario, 'apePaterno'),
            apeMaterno: this.getElementTextNS(usuario, 'apeMaterno'),
            correo: this.getElementTextNS(usuario, 'correo'),
            tipoDoc: this.getElementTextNS(usuario, 'tipoDoc') as any,
            nroDoc: this.getElementTextNS(usuario, 'nroDoc'),
            direccion: this.getElementTextNS(usuario, 'direccion'),
            telefono: this.getElementTextNS(usuario, 'telefono'),
            idDistrito: this.parseIntOrUndefined(
              this.getElementTextNS(usuario, 'idDistrito'),
            ),
            idRol:
              this.parseIntOrUndefined(
                this.getElementTextNS(usuario, 'idRol'),
              ) ?? 0,
            nombreRol: this.getElementTextNS(usuario, 'nombreRol'),
            activo:
              this.getElementTextNS(usuario, 'activo').toLowerCase() === 'true',
            fechaRegistro: this.getElementTextNS(usuario, 'fechaRegistro'),
          });
        }

        return usuarios;
      }),
    );
  }

  // ================= OBTENER USUARIO =================
  obtenerUsuario(idUsuario: number): Observable<any> {
    const xml = `
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:usu="http://koroFoods.com/usuario">
 <soapenv:Header/>
 <soapenv:Body>
  <usu:obtenerUsuarioRequest>
    <usu:idUsuario>${idUsuario}</usu:idUsuario>
  </usu:obtenerUsuarioRequest>
 </soapenv:Body>
</soapenv:Envelope>`;

    return this.send(xml).pipe(
      map((response) => {
        const parser = new DOMParser();
        const doc = this.parseXmlResponse(response, parser);

        return {
          exitoso: this.getTextContent(doc, 'exitoso') === 'true',
          mensaje: this.getTextContent(doc, 'mensaje'),
          usuario: this.parseUsuarioFromXml(doc),
        };
      }),
    );
  }

  // ================= ACTUALIZAR USUARIO =================
  actualizarUsuario(
    idUsuario: number,
    data: UsuarioSoap,
  ): Observable<RespuestaSoap> {
    const xml = `
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:usu="http://koroFoods.com/usuario">
 <soapenv:Header/>
 <soapenv:Body>
  <usu:actualizarUsuarioRequest>
    <usu:idUsuario>${idUsuario}</usu:idUsuario>
    <usu:nombres>${data.nombres}</usu:nombres>
    <usu:apePaterno>${data.apePaterno}</usu:apePaterno>
    <usu:apeMaterno>${data.apeMaterno}</usu:apeMaterno>
    <usu:correo>${data.correo}</usu:correo>
    ${data.clave ? `<usu:clave>${data.clave}</usu:clave>` : '<usu:clave></usu:clave>'}
    <usu:tipoDoc>${data.tipoDoc}</usu:tipoDoc>
    <usu:nroDoc>${data.nroDoc}</usu:nroDoc>
    ${data.direccion ? `<usu:direccion>${data.direccion}</usu:direccion>` : ''}
    ${data.telefono ? `<usu:telefono>${data.telefono}</usu:telefono>` : ''}
    ${data.idDistrito ? `<usu:idDistrito>${data.idDistrito}</usu:idDistrito>` : ''}
    <usu:idRol>${data.idRol}</usu:idRol>
    <usu:activo>${data.activo}</usu:activo>
  </usu:actualizarUsuarioRequest>
 </soapenv:Body>
</soapenv:Envelope>`;

    return this.send(xml).pipe(
      map((response) => {
        const parser = new DOMParser();
        const doc = this.parseXmlResponse(response, parser);

        const exitosoNode = Array.from(doc.getElementsByTagName('*')).find(
          (n) => n.localName === 'exitoso',
        );
        const mensajeNode = Array.from(doc.getElementsByTagName('*')).find(
          (n) => n.localName === 'mensaje',
        );
        const codigoNode = Array.from(doc.getElementsByTagName('*')).find(
          (n) => n.localName === 'codigo',
        );

        return {
          exitoso: exitosoNode?.textContent === 'true',
          mensaje: mensajeNode?.textContent || '',
          codigo: codigoNode?.textContent || undefined,
        };
      }),
    );
  }

  // ================= CAMBIAR ESTADO =================
  cambiarEstadoUsuario(
    idUsuario: number,
    activo: boolean,
  ): Observable<RespuestaSoap> {
    const xml = `
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:usu="http://koroFoods.com/usuario">
 <soapenv:Header/>
 <soapenv:Body>
  <usu:cambiarEstadoUsuarioRequest>
    <usu:idUsuario>${idUsuario}</usu:idUsuario>
    <usu:activo>${activo}</usu:activo>
  </usu:cambiarEstadoUsuarioRequest>
 </soapenv:Body>
</soapenv:Envelope>`;

    return this.send(xml).pipe(
      map((response) => {
        const parser = new DOMParser();
        const doc = this.parseXmlResponse(response, parser);

        const exitosoNode = Array.from(doc.getElementsByTagName('*')).find(
          (n) => n.localName === 'exitoso',
        );
        const mensajeNode = Array.from(doc.getElementsByTagName('*')).find(
          (n) => n.localName === 'mensaje',
        );
        const codigoNode = Array.from(doc.getElementsByTagName('*')).find(
          (n) => n.localName === 'codigo',
        );

        const exitoso = exitosoNode?.textContent === 'true';
        const mensaje = mensajeNode?.textContent || '';
        const codigo = codigoNode?.textContent || undefined;

        const respuesta: RespuestaSoap = { exitoso, mensaje, codigo };
        return respuesta;
      }),
    );
  }

  private parseUsuarioFromXml(doc: Document): UsuarioSoap | null {
    const usuarioElement = doc.getElementsByTagName('usuario')[0];
    if (!usuarioElement) return null;

    return {
      idUsuario:
        this.parseIntOrUndefined(
          this.getElementTextNS(usuarioElement, 'idUsuario'),
        ) ?? 0,
      nombres: this.getElementTextNS(usuarioElement, 'nombres'),
      apePaterno: this.getElementTextNS(usuarioElement, 'apePaterno'),
      apeMaterno: this.getElementTextNS(usuarioElement, 'apeMaterno'),
      correo: this.getElementTextNS(usuarioElement, 'correo'),
      tipoDoc: this.getElementTextNS(usuarioElement, 'tipoDoc') as any,
      nroDoc: this.getElementTextNS(usuarioElement, 'nroDoc'),
      direccion: this.getElementTextNS(usuarioElement, 'direccion'),
      telefono: this.getElementTextNS(usuarioElement, 'telefono'),
      idDistrito: this.parseIntOrUndefined(
        this.getElementTextNS(usuarioElement, 'idDistrito'),
      ),
      idRol:
        this.parseIntOrUndefined(
          this.getElementTextNS(usuarioElement, 'idRol'),
        ) ?? 0,
      nombreRol: this.getElementTextNS(usuarioElement, 'nombreRol'),
      activo:
        this.getElementTextNS(usuarioElement, 'activo').toLowerCase() ===
        'true',
      fechaRegistro: this.getElementTextNS(usuarioElement, 'fechaRegistro'),
    };
  }
}
