import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiClientService } from './api-client.service';
import { 
  Incident, 
  IncidentCreateDto, 
  IncidentUpdateDto, 
  IncidentFilters, 
  StatusUpdateDto 
} from '../models/incident.model';
import { PageResponse, StatsResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class IncidentService {
  private readonly endpoint = '/incidents';

  constructor(private apiClient: ApiClientService) {}

  getIncidents(filters: IncidentFilters = {}): Observable<PageResponse<Incident>> {
    const params = this.buildQueryParams(filters);
    return this.apiClient.getPage<Incident>(this.endpoint, params);
  }

  getIncidentById(id: string): Observable<Incident> {
    return this.apiClient.getOne<Incident>(this.endpoint, id);
  }

  createIncident(incident: IncidentCreateDto): Observable<Incident> {
    const normalizedIncident = this.normalizeIncidentFormValue(incident);
    return this.apiClient.post<IncidentCreateDto, Incident>(this.endpoint, normalizedIncident);
  }

  updateIncident(id: string, incident: IncidentUpdateDto): Observable<Incident> {
    const normalizedIncident = this.normalizeIncidentFormValue(incident);
    return this.apiClient.put<IncidentUpdateDto, Incident>(this.endpoint, id, normalizedIncident);
  }

  updateIncidentStatus(id: string, status: StatusUpdateDto): Observable<Incident> {
    return this.apiClient.patch<StatusUpdateDto, Incident>(`${this.endpoint}/${id}/status`, '', status);
  }

  deleteIncident(id: string): Observable<void> {
    return this.apiClient.delete(this.endpoint, id);
  }

  getStats(): Observable<StatsResponse> {
    return this.apiClient.getOne<StatsResponse>('/stats/incidents');
  }

  private buildQueryParams(filters: IncidentFilters): any {
    const params: any = {};
    
    if (filters.status) params.status = filters.status;
    if (filters.priority) params.priority = filters.priority;
    if (filters.q) params.q = filters.q.trim();
    if (filters.page !== undefined) params.page = filters.page;
    if (filters.size !== undefined) params.size = filters.size;
    if (filters.sort) params.sort = filters.sort;
    
    return params;
  }

  private normalizeIncidentFormValue(formValue: any): any {
    const normalized = { ...formValue };
    
    if (normalized.title) {
      normalized.title = normalized.title.trim();
    }
    
    if (normalized.description) {
      normalized.description = normalized.description.trim();
    }
    
    if (normalized.responsibleEmail) {
      normalized.responsibleEmail = normalized.responsibleEmail.trim().toLowerCase();
    }
    
    if (normalized.tags) {
      normalized.tags = this.normalizeTags(normalized.tags);
    }
    
    return normalized;
  }

  private normalizeTags(tags: string[]): string[] {
    if (!Array.isArray(tags)) return [];
    
    return tags
      .filter(tag => tag && tag.trim())
      .map(tag => tag.trim().toLowerCase())
      .filter((tag, index, array) => array.indexOf(tag) === index)
      .sort();
  }
} 