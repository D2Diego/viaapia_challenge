import { Injectable } from '@angular/core';
import { IncidentFilters } from '../models/incident.model';

export interface FilterState {
  [key: string]: any;
}

export interface SortOption {
  value: string;
  label: string;
  field: string;
  direction: 'asc' | 'desc';
}

@Injectable({
  providedIn: 'root'
})
export class FilterService {
  private readonly defaultPageSize = 10;
  
  readonly sortOptions: SortOption[] = [
    { value: 'createdAt,desc', label: 'Newest First', field: 'createdAt', direction: 'desc' },
    { value: 'createdAt,asc', label: 'Oldest First', field: 'createdAt', direction: 'asc' },
    { value: 'title,asc', label: 'Title A-Z', field: 'title', direction: 'asc' },
    { value: 'title,desc', label: 'Title Z-A', field: 'title', direction: 'desc' },
    { value: 'priority,desc', label: 'Priority High-Low', field: 'priority', direction: 'desc' },
    { value: 'priority,asc', label: 'Priority Low-High', field: 'priority', direction: 'asc' },
    { value: 'status,asc', label: 'Status A-Z', field: 'status', direction: 'asc' },
    { value: 'updatedAt,desc', label: 'Recently Updated', field: 'updatedAt', direction: 'desc' }
  ];

  /**
   * Build query parameters from filter state and pagination (DRY principle)
   */
  buildQueryParams(
    filterState: FilterState, 
    currentPage: number = 0, 
    pageSize: number = this.defaultPageSize
  ): IncidentFilters {
    const params: IncidentFilters = {
      page: currentPage,
      size: pageSize
    };

    if (filterState['q']?.trim()) {
      params.q = filterState['q'].trim();
    }

    if (filterState['status']) {
      const statusValue = Array.isArray(filterState['status']) 
        ? filterState['status'].filter(s => s && s.trim()).join(',')
        : filterState['status'];
      if (statusValue) {
        params.status = statusValue;
      }
    }

    if (filterState['priority']) {
      const priorityValue = Array.isArray(filterState['priority'])
        ? filterState['priority'].filter(p => p && p.trim()).join(',')
        : filterState['priority'];
      if (priorityValue) {
        params.priority = priorityValue;
      }
    }

    if (filterState['sort']) {
      params.sort = filterState['sort'];
    } else {
      params.sort = 'createdAt,desc';
    }

    return params;
  }

  /**
   * Normalize form values (DRY principle)
   */
  normalizeFilterFormValue(formValue: any): FilterState {
    const normalized: FilterState = {};

    if (formValue['q']?.trim()) {
      normalized['q'] = formValue['q'].trim();
    }

    Object.keys(formValue).forEach(key => {
      if (key !== 'q' && formValue[key] && formValue[key] !== '' && formValue[key] !== null) {
        if (Array.isArray(formValue[key])) {
          const filteredArray = formValue[key].filter((item: any) => item && item !== '');
          if (filteredArray.length > 0) {
            normalized[key] = filteredArray;
          }
        } else {
          normalized[key] = formValue[key];
        }
      }
    });

    return normalized;
  }

  /**
   * Create URL query parameters for navigation
   */
  createUrlParams(filters: IncidentFilters): { [key: string]: any } {
    const urlParams: { [key: string]: any } = {};

    if (filters.status) urlParams['status'] = filters.status;
    if (filters.priority) urlParams['priority'] = filters.priority;
    if (filters.q) urlParams['q'] = filters.q;
    if (filters.page && filters.page > 0) urlParams['page'] = filters.page;
    if (filters.size && filters.size !== this.defaultPageSize) urlParams['size'] = filters.size;
    if (filters.sort && filters.sort !== 'createdAt,desc') urlParams['sort'] = filters.sort;

    return urlParams;
  }

  /**
   * Get default filter values
   */
  getDefaultFilters(): FilterState {
    return {
      q: '',
      status: '',
      priority: '',
      sort: 'createdAt,desc'
    };
  }

  /**
   * Check if filters are applied (not default)
   */
  hasActiveFilters(filterState: FilterState): boolean {
    const defaults = this.getDefaultFilters();
    return Object.keys(filterState).some(key => 
      filterState[key] && filterState[key] !== defaults[key]
    );
  }

  /**
   * Get sort option by value
   */
  getSortOption(value: string): SortOption | undefined {
    return this.sortOptions.find(option => option.value === value);
  }

  /**
   * Parse query params from route into filter state
   */
  parseRouteParams(params: any): { filters: FilterState; page: number; size: number } {
    const filters: FilterState = {};
    
    if (params['status']) filters['status'] = params['status'];
    if (params['priority']) filters['priority'] = params['priority'];
    if (params['q']) filters['q'] = params['q'];
    if (params['sort']) filters['sort'] = params['sort'];

    const page = params['page'] ? parseInt(params['page'], 10) : 0;
    const size = params['size'] ? parseInt(params['size'], 10) : this.defaultPageSize;

    return { filters, page, size };
  }
} 