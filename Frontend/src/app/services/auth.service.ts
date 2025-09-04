import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { LoginRequest, LoginResponse, User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly TOKEN_KEY = 'incident_management_token';
  private readonly USER_KEY = 'incident_management_user';
  
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());
  private currentUserSubject = new BehaviorSubject<User | null>(this.getCurrentUserFromStorage());

  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(loginRequest: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.apiBaseUrl}/login`, loginRequest)
      .pipe(
        tap(response => {
          console.log('Login successful, token received:', response.accessToken.substring(0, 50) + '...');
          this.setToken(response.accessToken);
          this.setTokenExpiration(response.expiresIn);
          this.isAuthenticatedSubject.next(true);
          // Decode user info from token and set current user
          this.setCurrentUserFromToken(response.accessToken);
          console.log('Token stored, user authenticated');
        })
      );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    localStorage.removeItem('token_expiration');
    this.isAuthenticatedSubject.next(false);
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    const token = localStorage.getItem(this.TOKEN_KEY);
    console.log('Getting token from localStorage:', token ? token.substring(0, 50) + '...' : 'null');
    return token;
  }

  private setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  private setTokenExpiration(expiresIn: number): void {
    const expirationTime = new Date().getTime() + (expiresIn * 1000);
    localStorage.setItem('token_expiration', expirationTime.toString());
  }

  private hasToken(): boolean {
    const token = this.getToken();
    if (!token) return false;
    
    const expiration = localStorage.getItem('token_expiration');
    if (!expiration) return false;
    
    return new Date().getTime() < parseInt(expiration);
  }

  private setCurrentUserFromToken(token: string): void {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const user: User = {
        userId: payload.sub,
        username: payload.username || 'user',
        roles: payload.scope ? [{ name: payload.scope }] : []
      };
      localStorage.setItem(this.USER_KEY, JSON.stringify(user));
      this.currentUserSubject.next(user);
    } catch (error) {
      console.error('Error parsing token:', error);
    }
  }

  private getCurrentUserFromStorage(): User | null {
    const userStr = localStorage.getItem(this.USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  hasRole(roleName: string): boolean {
    const user = this.getCurrentUser();
    return user?.roles?.some(role => role.name === roleName) || false;
  }

  isTokenExpired(): boolean {
    const expiration = localStorage.getItem('token_expiration');
    if (!expiration) return true;
    return new Date().getTime() >= parseInt(expiration);
  }
} 