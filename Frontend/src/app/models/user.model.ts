export interface User {
  userId?: string;
  username: string;
  roles?: Role[];
}

export interface Role {
  roleId?: number;
  name: string;
}

export interface UserCreateDto {
  username: string;
  password: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  expiresIn: number;
} 