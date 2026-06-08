import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

type ServerInfo = {
  port: number;
  token: string;
};

type HealthResponse = {
  status: string;
  app: string;
  version: string;
  database: string;
  timestamp: string;
};

@Injectable({
  providedIn: 'root',
})
export class ApiService {

  private serverInfo?: ServerInfo;

  private http = inject(HttpClient);   

  public async initialize(): Promise<void> {
    const electronApi = (Window as any).msrpg;

    if (electronApi?.getServerInfo) {
      this.serverInfo = await electronApi.getServerInfo();;
      return;
    }

    // Fallback para dev com ng serve
    this.serverInfo = {
      port: 8080,
      token: '',
    };
  }

  public health() {
    if (!this.serverInfo) {
      throw new Error("Falha a inicialização do ApiService.");
    }

    const headers = new HttpHeaders({
      "X-Session-Token": this.serverInfo.token,
    });

    return this.http.get<HealthResponse>(
      `http://localhost:${this.serverInfo.port}/health`, 
      { headers }
    );
  }

}
