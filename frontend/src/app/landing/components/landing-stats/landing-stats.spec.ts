import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LandingStats } from './landing-stats';

describe('LandingStats', () => {
  let component: LandingStats;
  let fixture: ComponentFixture<LandingStats>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LandingStats]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LandingStats);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
