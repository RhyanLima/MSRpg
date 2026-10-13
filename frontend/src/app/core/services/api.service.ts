import { Injectable, InjectionToken } from '@angular/core';

/** Dados de conexão com o backend Java local, entregues pelo processo Electron. */
export interface ServerInfo {
  readonly port: number;
  readonly token: string;
}

interface MsrpgBridge {
  getServerInfo(): Promise<ServerInfo>;
}

declare global {
  interface Window {
    msrpg?: MsrpgBridge;
  }
}

export const API_URL = new InjectionToken<string>('API_URL');

const DEV_FALLBACK: ServerInfo = { port: 8080, token: '' };
const BACKEND_HOST = 'http://localhost';

@Injectable({
  providedIn: 'root',
})
export class ApiService {

  private serverInfo?: ServerInfo;
  private initialization?: Promise<void>;


  public initialize(): Promise<void> {
    this.initialization ??= this.loadServerInfo();
    return this.initialization;
  }

  public get getServerInfo(): ServerInfo | undefined {
    return this.serverInfo;
  }

  public get sessionToken(): string {
    return this.serverInfo?.token ?? '';
  }

  public get backendOrigin(): string {
    return `${BACKEND_HOST}:${this.serverInfo?.port ?? DEV_FALLBACK.port}`;
  }

  public backendUrl(path: string): string {
    if (!path.startsWith('/api/')) {
      throw new Error('Backend paths must start with /api/.');
    }
    return `${this.backendOrigin}${path}`;
  }

  public isBackendUrl(url: string): boolean {
    return url.startsWith(`${this.backendOrigin}/`);
  }

  private async loadServerInfo(): Promise<void> {

    const bridge = window.msrpg;

    if (!bridge?.getServerInfo) {
      this.serverInfo = DEV_FALLBACK;
      return;
    }

    this.serverInfo = validated(await bridge.getServerInfo());
  }
}

function validated(info: ServerInfo | null | undefined): ServerInfo {
  const port = Number(info?.port);
  if (!Number.isInteger(port) || port < 1 || port > 65535) {
    throw new Error('Invalid backend port received from Electron.');
  }
  const token = typeof info?.token === 'string' ? info.token : '';
  return { port, token };
}
