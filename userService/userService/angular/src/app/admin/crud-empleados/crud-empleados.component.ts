import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { UsuarioSoap } from '../../shared/model/usuarioSoap,model';
import { UsuarioSoapService } from '../service/usuarioSoap.service';
import { CommonModule } from '@angular/common';
import { Distrito } from '../../shared/model/distrito.model';
import { DistritoService } from '../../auth/service/distrito.service';
import { AlertService } from '../../util/alert.service';

@Component({
  selector: 'app-crud-usuarios',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './crud-empleados.component.html',
  styleUrl: './crud-empleados.component.css',
})
export class CrudEmpleadosComponent implements OnInit {
  usuarios: UsuarioSoap[] = [];
  usuariosFiltrados: UsuarioSoap[] = [];
  usuariosPaginados: UsuarioSoap[] = [];
  filtroRol: number | null = null;
  filtroEstado: boolean | null = null;

  paginaActual = 1;
  itemsPorPagina = 10;
  totalPaginas = 0;
  usuarioForm!: FormGroup;
  mostrarModal = false;
  modoEdicion = false;
  usuarioSeleccionado: UsuarioSoap | null = null;
  cargando = false;
  idDistrito = null;
  distritos: Distrito[] = [];
  roles = [
    { id: 2, nombre: 'Recepcionista' },
    { id: 3, nombre: 'Mesero' },
  ];

  tiposDocumento = ['DNI', 'PAS', 'CDX', 'CMP'];

  constructor(
    private fb: FormBuilder,
    private usuarioService: UsuarioSoapService,
    private distritoService: DistritoService,
  ) {
    this.inicializarFormulario();
  }

  ngOnInit(): void {
    this.cargarUsuarios();
    this.cargarDistritos();
  }
  cargarDistritos() {
    this.distritoService.listarDistritos().subscribe({
      next: (resp) => (this.distritos = resp),
      error: (e) => console.error('Error al cargar distritos', e),
    });
  }
  inicializarFormulario(): void {
    this.usuarioForm = this.fb.group({
      nombres: ['', [Validators.required, Validators.minLength(2)]],
      apePaterno: ['', [Validators.required, Validators.minLength(2)]],
      apeMaterno: ['', [Validators.required, Validators.minLength(2)]],
      correo: ['', [Validators.required, Validators.email]],
      clave: ['', [Validators.required, Validators.minLength(6)]],
      tipoDoc: ['DNI', Validators.required],
      nroDoc: ['', [Validators.required, Validators.pattern(/^\d{8,12}$/)]],
      direccion: [''],
      idDistrito: [null, Validators.required],
      telefono: ['', Validators.pattern(/^\d{9}$/)],
      idRol: [2, Validators.required],
      activo: [true],
    });
  }

  cargarUsuarios() {
    this.cargando = true;
    this.usuarioService
      .listarUsuarios(this.filtroRol, this.filtroEstado)
      .subscribe({
        next: (data) => {
          this.usuarios = data;
          this.aplicarFiltrosYPaginacion();
          this.cargando = false;
        },
        error: (err) => {
          console.error('Error al cargar usuarios', err);
          this.cargando = false;
        },
      });
  }
  aplicarFiltrosYPaginacion() {
    this.usuariosFiltrados = [...this.usuarios];
    this.totalPaginas = Math.ceil(
      this.usuariosFiltrados.length / this.itemsPorPagina,
    );
    this.paginar();
  }

  paginar() {
    const inicio = (this.paginaActual - 1) * this.itemsPorPagina;
    const fin = inicio + this.itemsPorPagina;
    this.usuariosPaginados = this.usuariosFiltrados.slice(inicio, fin);
  }

  cambiarPagina(pagina: number) {
    if (pagina >= 1 && pagina <= this.totalPaginas) {
      this.paginaActual = pagina;
      this.paginar();
    }
  }

  filtrarPorRol(idRol: number | null) {
    this.filtroRol = idRol;
    this.paginaActual = 1;
    this.cargarUsuarios();
  }

  filtrarPorEstado(activo: boolean | null) {
    this.filtroEstado = activo;
    this.paginaActual = 1;
    this.cargarUsuarios();
  }

  limpiarFiltros() {
    this.filtroRol = null;
    this.filtroEstado = null;
    this.paginaActual = 1;
    this.cargarUsuarios();
  }

  get paginasArray(): number[] {
    return Array.from({ length: this.totalPaginas }, (_, i) => i + 1);
  }

  abrirModalNuevo(): void {
    this.modoEdicion = false;
    this.usuarioSeleccionado = null;
    this.usuarioForm.reset({
      tipoDoc: 'DNI',
      idRol: 2,
      activo: true,
    });
    this.usuarioForm
      .get('clave')
      ?.setValidators([Validators.required, Validators.minLength(6)]);
    this.mostrarModal = true;
  }

  abrirModalEditar(usuario: UsuarioSoap): void {
    this.modoEdicion = true;
    this.usuarioSeleccionado = usuario;

    this.usuarioForm.patchValue({
      nombres: usuario.nombres,
      apePaterno: usuario.apePaterno,
      apeMaterno: usuario.apeMaterno,
      correo: usuario.correo,
      tipoDoc: usuario.tipoDoc,
      nroDoc: usuario.nroDoc,
      direccion: usuario.direccion,
      idDistrito: usuario.idDistrito,
      telefono: usuario.telefono,
      idRol: usuario.idRol,
      activo: usuario.activo,
    });

    // Clave opcional en edición
    this.usuarioForm.get('clave')?.clearValidators();
    this.usuarioForm.get('clave')?.updateValueAndValidity();

    this.mostrarModal = true;
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.usuarioForm.reset();
    this.usuarioSeleccionado = null;
  }

  guardarUsuario(): void {
    if (this.usuarioForm.invalid) {
      Object.keys(this.usuarioForm.controls).forEach((key) => {
        this.usuarioForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.cargando = true;
    const formData = this.usuarioForm.value;
    if (this.modoEdicion && this.usuarioSeleccionado) {
      this.usuarioService
        .actualizarUsuario(this.usuarioSeleccionado.idUsuario!, formData)
        .subscribe({
          next: (response) => {
            if (response.exitoso) {
              AlertService.success(response.mensaje);
              this.cargarUsuarios();
              this.cerrarModal();
            } else {
              AlertService.error(response.mensaje);
            }
            this.cargando = false;
          },
          error: (error) => {
            console.error('Error al actualizar:', error);
            alert('Error al actualizar el usuario');
            this.cargando = false;
          },
        });
    } else {
      this.usuarioService.crearUsuario(formData).subscribe({
        next: (response) => {
          if (response.exitoso) {
            AlertService.success(response.mensaje);
            this.cargarUsuarios();
            this.cerrarModal();
          } else {
            AlertService.error(response.mensaje);
          }
          this.cargando = false;
        },
        error: (error) => {
          console.error('Error al crear:', error);
          this.cargando = false;
        },
      });
    }
  }

  async cambiarEstado(usuario: UsuarioSoap): Promise<void> {
  const nuevoEstado = !usuario.activo;
  const mensaje = nuevoEstado
    ? '¿Activar este usuario?'
    : '¿Desactivar este usuario?';

  try {
    const confirmado = await AlertService.confirm(mensaje);

    if (!confirmado) return;

    this.usuarioService
      .cambiarEstadoUsuario(usuario.idUsuario!, nuevoEstado)
      .subscribe({
        next: (response) => {
          console.log("Response completo:", response);

          if (response.exitoso) {
            AlertService.success(response.mensaje);
            this.cargarUsuarios();
          } else {
            AlertService.error(response.mensaje);
            console.warn("Error de negocio:", response);
          }
        },
        error: (err) => {
          console.error("Error HTTP:", err);
          AlertService.error("Ocurrió un error al cambiar el estado.");
        },
      });
  } catch (error) {
    console.error("Error inesperado:", error);
    AlertService.error("Error inesperado.");
  }
}


  obtenerNombreRol(idRol: number): string {
    const rol = this.roles.find((r) => r.id === idRol);
    return rol ? rol.nombre : 'Desconocido';
  }

  getNombreCompleto(usuario: UsuarioSoap): string {
    return `${usuario.nombres} ${usuario.apePaterno} ${usuario.apeMaterno}`;
  }
}
