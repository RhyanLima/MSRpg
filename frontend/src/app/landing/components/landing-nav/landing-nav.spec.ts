import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LandingNav } from './landing-nav';

describe('LandingNav', () => {
  let component: LandingNav;
  let fixture: ComponentFixture<LandingNav>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LandingNav]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LandingNav);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
