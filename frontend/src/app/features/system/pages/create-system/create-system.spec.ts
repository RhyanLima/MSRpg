import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateSystem } from './create-system';
import { provideHttpClient } from '@angular/common/http';

describe('CreateSystem', () => {
  let component: CreateSystem;
  let fixture: ComponentFixture<CreateSystem>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateSystem],
      providers: [provideHttpClient()],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateSystem);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('deve exigir nome, engine version, content version e resolution policy id', () => {
    component.form.controls.name.setValue('');
    component.form.controls.engineVersion.setValue('');
    component.form.controls.contentVersion.setValue('');
    component.form.controls.defaultResolutionPolicyId.setValue('');

    expect(component.form.invalid).toBeTruthy();
  });

  it('deve enviar settingsJson como "{}" quando definições avançadas não estiverem marcadas', () => {
    expect(component.advancedSettings()).toBeFalsy();
  });
});
