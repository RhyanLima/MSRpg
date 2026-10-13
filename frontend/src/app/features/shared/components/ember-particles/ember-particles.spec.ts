import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmberParticles } from './ember-particles';

describe('EmberParticles', () => {
  let component: EmberParticles;
  let fixture: ComponentFixture<EmberParticles>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmberParticles],
    }).compileComponents();

    fixture = TestBed.createComponent(EmberParticles);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
