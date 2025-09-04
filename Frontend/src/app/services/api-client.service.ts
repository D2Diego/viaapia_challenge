import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { ApiResponse, PageResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class ApiClientService {

  constructor(private http: HttpClient) {}

  getPage<T>(endpoint: string, params?: any): Observable<PageResponse<T>> {
    const httpParams = this.buildHttpParams(params);
    return this.http.get<ApiResponse<PageResponse<T>>>(`${environment.apiBaseUrl}${endpoint}`, { params: httpParams })
      .pipe(
        map(response => response.data!),
        catchError(this.handleError)
      );
  }

  getOne<T>(endpoint: string, id?: string): Observable<T> {
    const url = id ? `${environment.apiBaseUrl}${endpoint}/${id}` : `${environment.apiBaseUrl}${endpoint}`;
    return this.http.get<ApiResponse<T>>(url)
      .pipe(
        map(response => response.data!),
        catchError(this.handleError)
      );
  }

  getList<T>(endpoint: string, params?: any): Observable<T[]> {
    const httpParams = this.buildHttpParams(params);
    return this.http.get<ApiResponse<T[]>>(`${environment.apiBaseUrl}${endpoint}`, { params: httpParams })
      .pipe(
        map(response => response.data!),
        catchError(this.handleError)
      );
  }

  post<T, R = T>(endpoint: string, data: T): Observable<R> {
    return this.http.post<ApiResponse<R>>(`${environment.apiBaseUrl}${endpoint}`, data)
      .pipe(
        map(response => response.data!),
        catchError(this.handleError)
      );
  }

  put<T, R = T>(endpoint: string, id: string, data: T): Observable<R> {
    return this.http.put<ApiResponse<R>>(`${environment.apiBaseUrl}${endpoint}/${id}`, data)
      .pipe(
        map(response => response.data!),
        catchError(this.handleError)
      );
  }

  patch<T, R = T>(endpoint: string, id: string, data: T): Observable<R> {
    return this.http.patch<ApiResponse<R>>(`${environment.apiBaseUrl}${endpoint}/${id}`, data)
      .pipe(
        map(response => response.data!),
        catchError(this.handleError)
      );
  }

  delete(endpoint: string, id: string): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${environment.apiBaseUrl}${endpoint}/${id}`)
      .pipe(
        map(() => void 0),
        catchError(this.handleError)
      );
  }

  private buildHttpParams(params?: any): HttpParams {
    let httpParams = new HttpParams();
    
    if (params) {
      Object.keys(params).forEach(key => {
        const value = params[key];
        if (value !== null && value !== undefined && value !== '') {
          httpParams = httpParams.set(key, value.toString());
        }
      });
    }
    
    return httpParams;
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unknown error occurred';
    
    if (error.error instanceof ErrorEvent) {
      errorMessage = error.error.message;
    } else {
      if (error.error?.message) {
        errorMessage = error.error.message;
      } else if (error.status === 401) {
        errorMessage = 'Unauthorized access. Please login again.';
      } else if (error.status === 403) {
        errorMessage = 'Access forbidden. Insufficient permissions.';
      } else if (error.status === 404) {
        errorMessage = 'Resource not found.';
      } else if (error.status === 500) {
        errorMessage = 'Internal server error. Please try again later.';
      } else {
        errorMessage = `Error ${error.status}: ${error.statusText}`;
      }
    }
    
    console.error('API Error:', error);
    return throwError(() => new Error(errorMessage));
  }
} 