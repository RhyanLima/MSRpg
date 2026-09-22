import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LandingRulesEngine } from './landing-rules-engine';

describe('LandingRulesEngine', () => {
  let component: LandingRulesEngine;
  let fixture: ComponentFixture<LandingRulesEngine>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LandingRulesEngine]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LandingRulesEngine);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
