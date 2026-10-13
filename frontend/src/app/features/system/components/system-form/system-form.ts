import { ChangeDetectionStrategy, Component, computed, input, linkedSignal, output } from '@angular/core';
import {
  FormField,
  form,
  maxLength,
  minLength,
  pattern,
  required,
  submit,
  validate,
} from '@angular/forms/signals';
import {
  CONFLICT_STRATEGY_OPTIONS,
  EMPTY_DRAFT,
  MISSING_COMPONENT_OPTIONS,
  OptionItem,
  RpgSystemDraft,
  SEMVER_PATTERN,
  SYNC_POLICY_OPTIONS,
  isOption,
} from '../../models/rpg-system.model';

const NAME_MIN = 3;
const NAME_MAX = 100;
const DESCRIPTION_MAX = 2000;
// eslint-disable-next-line no-control-regex
const CONTROL_CHARS = /[\u0000-\u001F\u007F]/;

/**
 * Formulário de sistema (criação e edição). Regras espelham as do domínio
 * no backend; o servidor continua sendo a fonte de verdade e revalida tudo.
 *
 * Apresentacional: não faz HTTP. Emite o rascunho válido em (saved).
 */
@Component({
  selector: 'app-system-form',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormField],
  templateUrl: './system-form.html',
  styleUrl: './system-form.scss',
})
export class SystemForm {

  readonly initial = input<RpgSystemDraft>(EMPTY_DRAFT);
  readonly submitLabel = input('Salvar');
  readonly busy = input(false);

  readonly saved = output<RpgSystemDraft>();

  protected readonly syncPolicies = SYNC_POLICY_OPTIONS;
  protected readonly missingComponentPolicies = MISSING_COMPONENT_OPTIONS;
  protected readonly conflictStrategies = CONFLICT_STRATEGY_OPTIONS;
  protected readonly limits = { nameMin: NAME_MIN, nameMax: NAME_MAX, descriptionMax: DESCRIPTION_MAX };

  /** Reinicia o modelo sempre que um novo valor inicial chega (ex.: sistema carregado). */
  protected readonly model = linkedSignal<RpgSystemDraft>(() => ({ ...this.initial() }));

  protected readonly systemForm = form(this.model, path => {
    required(path.name, { message: 'Informe o nome do sistema.' });
    minLength(path.name, NAME_MIN, { message: `O nome precisa de pelo menos ${NAME_MIN} caracteres.` });
    maxLength(path.name, NAME_MAX, { message: `O nome pode ter no máximo ${NAME_MAX} caracteres.` });
    validate(path.name, ({ value }) =>
      CONTROL_CHARS.test(value()) ? { kind: 'controlChars', message: 'O nome contém caracteres inválidos.' } : undefined);

    maxLength(path.description, DESCRIPTION_MAX,
      { message: `A descrição pode ter no máximo ${DESCRIPTION_MAX} caracteres.` });

    required(path.contentVersion, { message: 'Informe a versão do conteúdo.' });
    pattern(path.contentVersion, SEMVER_PATTERN, { message: 'Use o formato MAJOR.MINOR.PATCH (ex.: 1.0.0).' });

    validate(path.defaultSyncPolicy, oneOf(SYNC_POLICY_OPTIONS));
    validate(path.missingComponentPolicy, oneOf(MISSING_COMPONENT_OPTIONS));
    validate(path.conflictResolutionStrategy, oneOf(CONFLICT_STRATEGY_OPTIONS));
  });

  protected readonly canSubmit = computed(() => !this.busy() && this.systemForm().valid());

  protected onSubmit(event: Event): void {
    event.preventDefault();
    void submit(this.systemForm, async () => {
      this.saved.emit(this.model());
    });
  }
}

function oneOf<T extends string>(options: readonly OptionItem<T>[]) {
  return ({ value }: { value: () => string }) =>
    isOption(options, value()) ? undefined : { kind: 'option', message: 'Selecione uma opção válida.' };
}
