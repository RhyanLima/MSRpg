import { HttpErrorResponse } from '@angular/common/http';

export type ErrorMessages = Readonly<Partial<Record<number, string>>> & { readonly fallback: string };

const MAX_DETAIL_LENGTH = 300;

export function describeHttpError(error: unknown, messages: ErrorMessages): string {
  if (!(error instanceof HttpErrorResponse)) {
    return messages.fallback;
  }
  if (error.status === 0) {
    return 'Servidor indisponível. Verifique se o MSRpg está em execução.';
  }
  if (error.status === 401) {
    return 'Sessão local inválida. Reinicie o aplicativo.';
  }
  const specific = messages[error.status];
  if (specific) {
    return specific;
  }
  if (error.status === 400) {
    const detail = serverDetail(error);
    return detail ? `Dados inválidos: ${detail}` : 'Dados inválidos. Revise o formulário.';
  }
  return messages.fallback;
}

function serverDetail(error: HttpErrorResponse): string | null {
  const message: unknown = error.error?.message;
  if (typeof message !== 'string' || message.trim() === '') {
    return null;
  }
  return message.trim().slice(0, MAX_DETAIL_LENGTH);
}
