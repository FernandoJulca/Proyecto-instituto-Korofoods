import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminLayoutComponent } from '../layout/admin-layout/admin-layout.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { EventoListComponent } from './crudEventos/event-list/evento.list.component';
import { EventoFormComponent } from './crudEventos/event-form/evento.form.component';

import { CrudEmpleadosComponent } from './crud-empleados/crud-empleados.component';
import { PlatoListComponent } from './crudMenus/menu-list/plato-list.component';
import { PlatoFormComponent } from './crudMenus/menu-form/plato-form.component';
import { MesaListComponent } from './crudMesas/mesa-list/mesa-list.component';
import { MesaFormComponent } from './crudMesas/mesa-form/mesa-form.component';
import { ReporteReservasComponent } from './reportes/reporte-reservas/reporte-reservas.component';
import { ReportesComponent } from './reportes/reportes.component';


const routes: Routes = [
  {
    path:'',
    component: AdminLayoutComponent,
    children: [
      {path: '', redirectTo: 'dashboard', pathMatch: 'full'},
      {
        path: 'dashboard',
        component: DashboardComponent,
        data: {title: 'Dashboard'},
      },
       // Rutas de Eventos
      { path: '', redirectTo: 'eventos', pathMatch: 'full' },
      { path: 'eventos', component: EventoListComponent },
      { path: 'eventos/nuevo', component: EventoFormComponent },
      { path: 'eventos/editar/:id', component: EventoFormComponent },
      // Menus
      { path: 'menus', component: PlatoListComponent },
      { path: 'menus/nuevo', component: PlatoFormComponent },
      { path: 'menus/editar/:id', component: PlatoFormComponent },
      // Mesas
      { path: 'mesas', component: MesaListComponent },
      { path: 'mesas/nuevo', component: MesaFormComponent },
      { path: 'mesas/editar/:id', component: MesaFormComponent },
      {
        path:'empleado', component:CrudEmpleadosComponent
      },
      {
        path: 'reportes',
        component: ReportesComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
