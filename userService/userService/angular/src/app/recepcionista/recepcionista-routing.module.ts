import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RecepcionistaLayoutComponent } from '../layout/recepcionista-layout/recepcionista-layout.component';
import { ChatContainerRecComponent } from './chat/chat-container-rec.component';
import { ListadoReservasAsistidasComponent } from './listado-reservas-asistidas/listado-reservas-asistidas.component';
import { DashboardComponent } from './dashboard/dashboard.component';

const routes: Routes = [
  {
    path: '',
    component: RecepcionistaLayoutComponent,
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
      {
        path: 'dashboard',
        component: DashboardComponent,
        data: { title: 'Dashboard' },
      },
      {
        path: 'chat',
        component: ChatContainerRecComponent,
        data: { title: 'Ordenes' },
      },
      {
        path: 'listado',
        component: ListadoReservasAsistidasComponent,
        data: { title: 'Listado' },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RecepcionistaRoutingModule {}
