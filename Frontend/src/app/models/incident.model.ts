export interface Incident {
  id?: string;
  title: string;
  description?: string;
  priority: IncidentPriority;
  status: IncidentStatus;
  responsibleEmail: string;
  tags?: string[];
  createdAt?: string;
  updatedAt?: string;
}

export interface IncidentCreateDto {
  title: string;
  description?: string;
  priority: IncidentPriority;
  status: IncidentStatus;
  responsibleEmail: string;
  tags?: string[];
}

export interface IncidentUpdateDto {
  title?: string;
  description?: string;
  priority?: IncidentPriority;
  status?: IncidentStatus;
  responsibleEmail?: string;
  tags?: string[];
}

export interface StatusUpdateDto {
  status: IncidentStatus;
}

export enum IncidentPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH'
}

export enum IncidentStatus {
  OPEN = 'OPEN',
  IN_PROGRESS = 'IN_PROGRESS',
  RESOLVED = 'RESOLVED',
  CANCELLED = 'CANCELLED'
}

export interface IncidentFilters {
  status?: IncidentStatus;
  priority?: IncidentPriority;
  q?: string;
  page?: number;
  size?: number;
  sort?: string;
} 