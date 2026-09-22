import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LandingHow } from './landing-how';

describe('LandingHow', () => {
  let component: LandingHow;
  let fixture: ComponentFixture<LandingHow>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LandingHow]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LandingHow);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
