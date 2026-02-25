import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ClienteLayoutComponent } from '../layout/cliente-layout/cliente-layout.component';
import { IndexComponent } from './index/index.component';
import { ResenaComponent } from './resena/resena.component';
import { FormResenaComponent } from './form-resena/form-resena.component';
import { MenuComponent } from './menu/menu.component';
import { ContactoComponent } from './contacto/contacto.component';
import { ReservaComponent } from './reserva/reserva.component';
import { ChatContainerComponent } from './chat/chat-container.component';
import { EventosTematicosComponent } from './eventos-tematicos/eventos-tematicos.component';
import { MisReservasComponent } from './mis-reservas/mis-reservas.component';
import { ListaPedidosComponent } from './pedido/lista-pedidos/lista-pedidos.component';
import { DetallePedidoComponent } from './pedido/detalle-pedido/detalle-pedido.component';
import { PerfilComponent } from './perfil/perfil.component';

const routes: Routes = [
  //Ruta para el chat sin layout
  {
    path: 'chat',
    component: ChatContainerComponent,
    data: { title: 'Chat' },
  },

  //Rutas con el layout presente
  {
    path: '',
    component: ClienteLayoutComponent,
    children: [
      {
        path: 'inicio',
        component: IndexComponent,
        data: { title: 'Inicio' },
      },
      {
        path: 'resenia',
        component: ResenaComponent,
        data: { title: 'Reseña' },
      },
      {
        path: 'crear-resenia',
        component: FormResenaComponent,
        data: { title: 'Crear Reseña' },
      },
      {
        path: 'menu',
        component: MenuComponent,
        data: { title: 'Menú' },
      },
      {
        path: 'contacto',
        component: ContactoComponent,
        data: { title: 'Contacto' },
      },
      {
        path: 'reserva',
        component: ReservaComponent,
        data: { title: 'Reserva' },
      },
      {
        path: 'pedido',
        component: ListaPedidosComponent,
        data: { title: 'Pedido' },
      },
      {
        path: 'pedido/:id',
        component: DetallePedidoComponent,
        data: { title: 'Detalle Pedido' },
      },
      {
        path: 'perfil',
        component: PerfilComponent,
        data: { title: 'Perfil' },
      },
      {
        path: 'eventos-tematicos',
        component: EventosTematicosComponent,
        data: { title: 'Eventos' },
      },
      {
        path: 'mis-reservas',
        component: MisReservasComponent,
        data: { title: 'Historial Reservas' },
      }
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ClienteRoutingModule {}
