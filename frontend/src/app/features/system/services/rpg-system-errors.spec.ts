import { HttpErrorResponse } from '@angular/common/http';
import { describeHttpError } from './rpg-system-errors';

const messages = { 409: 'Nome já usado.', fallback: 'Falhou.' };

function httpError(status: number, error: unknown = null): HttpErrorResponse {
  return new HttpErrorResponse({ status, error });
}

describe('describeHttpError', () => {
  it('usa a mensagem específica da operação', () => {
    expect(describeHttpError(httpError(409), messages)).toBe('Nome já usado.');
  });

  it('trata servidor indisponível e sessão inválida', () => {
    expect(describeHttpError(httpError(0), messages)).toMatch(/indisponível/);
    expect(describeHttpError(httpError(401), messages)).toMatch(/Sessão local/);
  });

  it('exibe o detalhe do servidor em 400, truncado', () => {
    const long = 'x'.repeat(500);

    const message = describeHttpError(httpError(400, { message: long }), messages);

    expect(message.startsWith('Dados inválidos: ')).toBe(true);
    expect(message.length).toBeLessThanOrEqual('Dados inválidos: '.length + 300);
  });

  it('cai no fallback para erros desconhecidos', () => {
    expect(describeHttpError(new Error('boom'), messages)).toBe('Falhou.');
    expect(describeHttpError(httpError(500), messages)).toBe('Falhou.');
  });
});
