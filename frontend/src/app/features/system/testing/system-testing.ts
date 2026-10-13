import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { SystemShell } from '../components/system-shell/system-shell';
import { RpgSystem, RpgSystemSummary } from '../models/rpg-system.model';

// host extra evita colisão de ID com o componente real
@Component({ selector: 'app-ember-particles', template: '', host: { 'data-test-stub': '' } })
class EmberParticlesStub {}

/** Troca as partículas animadas (canvas/rAF) por um stub nos testes de página. */
export function stubShellDecorations(): void {
  TestBed.overrideComponent(SystemShell, { set: { imports: [EmberParticlesStub] } });
}

export const API = 'http://localhost:8080/api/v1/rpg-systems';
export const SYSTEM_ID = '6f1c5f0e-7a3e-4f6a-9d7a-1c2b3d4e5f60';

export function aSummary(overrides: Partial<RpgSystemSummary> = {}): RpgSystemSummary {
  return {
    id: SYSTEM_ID,
    name: 'Tormenta',
    description: 'Fantasia sombria',
    engineVersion: '0.1.0',
    contentVersion: '1.0.0',
    createdAt: '2026-10-01T12:00:00Z',
    updatedAt: null,
    ...overrides,
  };
}

export function aSystem(overrides: Partial<RpgSystem> = {}): RpgSystem {
  return {
    ...aSummary(),
    defaultSyncPolicy: 'APPLY_TO_NEW_ONLY',
    missingComponentPolicy: 'WARN_AND_SKIP_STEP',
    conflictResolutionStrategy: 'ASK_USER',
    ...overrides,
  };
}

/** Preenche um input/textarea/select e dispara o evento que o [formField] escuta. */
export function typeInto(element: HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement, value: string): void {
  element.value = value;
  element.dispatchEvent(new Event('input'));
  if (element instanceof HTMLSelectElement) {
    element.dispatchEvent(new Event('change'));
  }
  element.dispatchEvent(new Event('blur'));
}
