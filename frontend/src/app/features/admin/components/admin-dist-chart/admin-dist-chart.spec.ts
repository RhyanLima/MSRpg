import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminDistChart } from './admin-dist-chart';

describe('AdminDistChart', () => {
  let component: AdminDistChart;
  let fixture: ComponentFixture<AdminDistChart>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminDistChart],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminDistChart);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
