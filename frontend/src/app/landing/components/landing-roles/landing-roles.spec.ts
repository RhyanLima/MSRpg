import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LandingRoles } from './landing-roles';

describe('LandingRoles', () => {
  let component: LandingRoles;
  let fixture: ComponentFixture<LandingRoles>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LandingRoles]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LandingRoles);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
