import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminKpiGrid } from './admin-kpi-grid';

describe('AdminKpiGrid', () => {
  let component: AdminKpiGrid;
  let fixture: ComponentFixture<AdminKpiGrid>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminKpiGrid],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminKpiGrid);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
