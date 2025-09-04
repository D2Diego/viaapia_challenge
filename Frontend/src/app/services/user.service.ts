import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiClientService } from './api-client.service';
import { User, UserCreateDto } from '../models/user.model';
import { PageResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly endpoint = '/users';

  constructor(private apiClient: ApiClientService) {}

  // Get paginated users
  getUsers(page = 0, size = 10, sort = 'username,asc'): Observable<PageResponse<User>> {
    const params = { page, size, sort };
    return this.apiClient.getPage<User>(this.endpoint, params);
  }

  // Get single user by ID
  getUserById(id: string): Observable<User> {
    return this.apiClient.getOne<User>(this.endpoint, id);
  }

  // Get user by username
  getUserByUsername(username: string): Observable<User> {
    return this.apiClient.getList<User>(`${this.endpoint}/search`, { username }).pipe(
      map((users: User[]) => users[0])
    );
  }

  // Create new user (public registration)
  createUser(user: UserCreateDto): Observable<User> {
    const normalizedUser = this.normalizeUserFormValue(user);
    return this.apiClient.post<UserCreateDto, User>(this.endpoint, normalizedUser);
  }

  // Update existing user
  updateUser(id: string, user: UserCreateDto): Observable<User> {
    const normalizedUser = this.normalizeUserFormValue(user);
    return this.apiClient.put<UserCreateDto, User>(this.endpoint, id, normalizedUser);
  }

  // Delete user
  deleteUser(id: string): Observable<void> {
    return this.apiClient.delete(this.endpoint, id);
  }

  // Normalize user form values (DRY principle)
  private normalizeUserFormValue(formValue: UserCreateDto): UserCreateDto {
    return {
      username: formValue.username?.trim().toLowerCase() || '',
      password: formValue.password || ''
    };
  }
} 