import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { ApiService } from '../services/api.service';

const SESSION_HEADER = 'X-Session-Token';

export const backendSessionInterceptor: HttpInterceptorFn = (request, next) => {
  const api = inject(ApiService);
  const token = api.sessionToken;

  if (!token || !api.isBackendUrl(request.url)) {
    return next(request);
  }

  return next(request.clone({ setHeaders: { [SESSION_HEADER]: token } }));
};
