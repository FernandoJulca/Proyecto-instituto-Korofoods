import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MeseroLayoutComponent } from '../layout/mesero-layout/mesero-layout.component';
import { OrdenesComponent } from './ordenes/ordenes.component';
import { FormOrdenComponent } from './form-orden/form-orden.component';
import { DetalleOrdenesComponent } from './ordenes/detalle-ordenes/detalle-ordenes.component';

const routes: Routes = [
  {
    path: '',
    component: MeseroLayoutComponent,
    children: [
      {
        path: 'ordenes',
        component: OrdenesComponent,
        data: { title: 'Ordenes' },
      },
      {
        path: 'ordenes/:id',
        component: DetalleOrdenesComponent,
        data: { title: 'Detalle Orden' },
      },
      {
        path: 'nueva-orden',
        component: FormOrdenComponent,
        data: { title: 'Nueva Orden' },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class MeseroRoutingModule {}
