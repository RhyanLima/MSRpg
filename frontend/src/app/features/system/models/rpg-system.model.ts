export type SyncPolicy = 'APPLY_TO_NEW_ONLY' | 'APPLY_TO_NEXT_CAMPAIGN' | 'APPLY_TO_NEXT_SESSION';
export type MissingComponentPolicy = 'WARN_AND_SKIP_STEP' | 'FAIL_EVENT' | 'IGNORE_SILENTLY';
export type ConflictResolutionStrategy = 'ASK_USER' | 'SKIP' | 'OVERWRITE' | 'CREATE_COPY';

export interface RpgSystemSummary {
  readonly id: string;
  readonly name: string;
  readonly description: string | null;
  readonly engineVersion: string;
  readonly contentVersion: string;
  readonly createdAt: string;
  readonly updatedAt: string | null;
}

export interface RpgSystem extends RpgSystemSummary {
  readonly defaultSyncPolicy: SyncPolicy;
  readonly missingComponentPolicy: MissingComponentPolicy;
  readonly conflictResolutionStrategy: ConflictResolutionStrategy;
}

export interface RpgSystemRequest {
  readonly name: string;
  readonly description: string;
  readonly contentVersion: string;
  readonly defaultSyncPolicy: SyncPolicy;
  readonly missingComponentPolicy: MissingComponentPolicy;
  readonly conflictResolutionStrategy: ConflictResolutionStrategy;
}

export interface RpgSystemFilter {
  readonly name?: string;
}

export interface RpgSystemDraft {
  readonly name: string;
  readonly description: string;
  readonly contentVersion: string;
  readonly defaultSyncPolicy: string;
  readonly missingComponentPolicy: string;
  readonly conflictResolutionStrategy: string;
}

export interface OptionItem<T extends string> {
  readonly value: T;
  readonly label: string;
}

export const SYNC_POLICY_OPTIONS: readonly OptionItem<SyncPolicy>[] = [
  { value: 'APPLY_TO_NEW_ONLY', label: 'Somente a novos elementos' },
  { value: 'APPLY_TO_NEXT_CAMPAIGN', label: 'Na próxima campanha' },
  { value: 'APPLY_TO_NEXT_SESSION', label: 'Na próxima sessão' },
];

export const MISSING_COMPONENT_OPTIONS: readonly OptionItem<MissingComponentPolicy>[] = [
  { value: 'WARN_AND_SKIP_STEP', label: 'Avisar e pular a etapa' },
  { value: 'FAIL_EVENT', label: 'Falhar o evento' },
  { value: 'IGNORE_SILENTLY', label: 'Ignorar silenciosamente' },
];

export const CONFLICT_STRATEGY_OPTIONS: readonly OptionItem<ConflictResolutionStrategy>[] = [
  { value: 'ASK_USER', label: 'Perguntar ao usuário' },
  { value: 'SKIP', label: 'Ignorar' },
  { value: 'OVERWRITE', label: 'Sobrescrever' },
  { value: 'CREATE_COPY', label: 'Criar cópia' },
];

export const EMPTY_DRAFT: RpgSystemDraft = {
  name: '',
  description: '',
  contentVersion: '1.0.0',
  defaultSyncPolicy: 'APPLY_TO_NEW_ONLY',
  missingComponentPolicy: 'WARN_AND_SKIP_STEP',
  conflictResolutionStrategy: 'ASK_USER',
};

export const SEMVER_PATTERN = /^\d{1,9}\.\d{1,9}\.\d{1,9}$/;

export function draftOf(system: RpgSystem): RpgSystemDraft {
  return {
    name: system.name,
    description: system.description ?? '',
    contentVersion: system.contentVersion,
    defaultSyncPolicy: system.defaultSyncPolicy,
    missingComponentPolicy: system.missingComponentPolicy,
    conflictResolutionStrategy: system.conflictResolutionStrategy,
  };
}

export function isOption<T extends string>(options: readonly OptionItem<T>[], value: string): value is T {
  return options.some(option => option.value === value);
}

export function labelOf<T extends string>(options: readonly OptionItem<T>[], value: string): string {
  return options.find(option => option.value === value)?.label ?? value;
}

/** Converte o rascunho validado em requisição tipada; falha alto se um enum escapar da validação. */
export function toRequest(draft: RpgSystemDraft): RpgSystemRequest {
  const { defaultSyncPolicy, missingComponentPolicy, conflictResolutionStrategy } = draft;
  if (!isOption(SYNC_POLICY_OPTIONS, defaultSyncPolicy)
    || !isOption(MISSING_COMPONENT_OPTIONS, missingComponentPolicy)
    || !isOption(CONFLICT_STRATEGY_OPTIONS, conflictResolutionStrategy)) {
    throw new Error('Draft contains values outside the allowed options.');
  }
  return {
    name: draft.name.trim(),
    description: draft.description.trim(),
    contentVersion: draft.contentVersion.trim(),
    defaultSyncPolicy,
    missingComponentPolicy,
    conflictResolutionStrategy,
  };
}
