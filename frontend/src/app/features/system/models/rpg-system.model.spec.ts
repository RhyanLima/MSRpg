import { EMPTY_DRAFT, RpgSystemDraft, draftOf, labelOf, SYNC_POLICY_OPTIONS, toRequest } from './rpg-system.model';
import { aSystem } from '../testing/system-testing';

describe('rpg-system.model', () => {
  it('draftOf converte descrição nula em string vazia (Signal Forms não aceita null)', () => {
    expect(draftOf(aSystem({ description: null })).description).toBe('');
  });

  it('toRequest remove espaços externos e mantém enums válidos', () => {
    const draft: RpgSystemDraft = { ...EMPTY_DRAFT, name: '  Tormenta ', contentVersion: ' 2.0.0 ' };

    const request = toRequest(draft);

    expect(request.name).toBe('Tormenta');
    expect(request.contentVersion).toBe('2.0.0');
    expect(request.defaultSyncPolicy).toBe('APPLY_TO_NEW_ONLY');
  });

  it('toRequest recusa enum fora das opções', () => {
    expect(() => toRequest({ ...EMPTY_DRAFT, name: 'X', defaultSyncPolicy: 'APPLY_TO_CAMPAIGN' })).toThrow();
  });

  it('labelOf devolve o rótulo ou o próprio valor', () => {
    expect(labelOf(SYNC_POLICY_OPTIONS, 'APPLY_TO_NEXT_SESSION')).toBe('Na próxima sessão');
    expect(labelOf(SYNC_POLICY_OPTIONS, 'X')).toBe('X');
  });
});
