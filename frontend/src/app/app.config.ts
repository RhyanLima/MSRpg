import { ApplicationConfig, inject, provideAppInitializer, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

import { routes } from './app.routes';
import { API_URL, ApiService } from './core/services/api.service';
import { backendSessionInterceptor } from './core/http/backend-session-interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([backendSessionInterceptor])),
    // Garante porta/token do backend ANTES da primeira rota fazer requisições.
    provideAppInitializer(() => inject(ApiService).initialize()),
    { provide: API_URL, useValue: 'http://localhost:3000/api' }
  ]
};
