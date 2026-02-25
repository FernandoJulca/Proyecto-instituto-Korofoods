import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReporteReservasComponent } from './reporte-reservas.component';

describe('ReporteReservasComponent', () => {
  let component: ReporteReservasComponent;
  let fixture: ComponentFixture<ReporteReservasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReporteReservasComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReporteReservasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
