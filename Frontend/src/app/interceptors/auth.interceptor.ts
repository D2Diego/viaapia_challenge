import { inject } from '@angular/core';
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const isPublicEndpoint = req.url.includes('/login') || 
                           (req.url.includes('/users') && req.method === 'POST');
  
  let authReq = req;
  
  if (!isPublicEndpoint) {
    const token = authService.getToken();
    
    if (token) {
      if (authService.isTokenExpired()) {
        authService.logout();
        router.navigate(['/login']);
        return throwError(() => new Error('Token expired'));
      }
      
      authReq = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      });
      
      console.log('Token added to request:', req.url, 'Token:', token.substring(0, 50) + '...');
    } else {
      console.log('No token found for request:', req.url);
    }
  } else {
    console.log('Public endpoint, skipping token:', req.url);
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      console.log('HTTP Error:', error.status, error.message);
      
      if (error.status === 401 && !isPublicEndpoint) {
        console.log('401 Unauthorized, logging out...');
        authService.logout();
        router.navigate(['/login']);
      }
      
      return throwError(() => error);
    })
  );
}; 