import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth-service';
import { catchError, from, switchMap, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  
  const authService = inject(AuthService);
  const router = inject(Router);

  const isRefreshRequest = req.url.includes('/api/auth/refresh');
  const token = authService.accessToken();

  const authReq = token && !isRefreshRequest
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !isRefreshRequest) {
        return from(authService.refreshAccessToken()).pipe(
          switchMap((refreshed) => {
            if (!refreshed) {
              router.navigate(['/login']);
              return throwError(() => error);
            }
            const newToken = authService.accessToken();
            const retryReq = newToken
              ? req.clone({ setHeaders: { Authorization: `Bearer ${newToken}` } })
              : req;
            return next(retryReq);
          })
        );
      }
      return throwError(() => error);
    })
  );

};
