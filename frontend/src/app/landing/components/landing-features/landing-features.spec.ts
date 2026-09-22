import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LandingFeatures } from './landing-features';

describe('LandingFeatures', () => {
  let component: LandingFeatures;
  let fixture: ComponentFixture<LandingFeatures>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LandingFeatures]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LandingFeatures);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
