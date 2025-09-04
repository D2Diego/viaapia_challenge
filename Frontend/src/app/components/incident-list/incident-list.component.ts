import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatChipsModule } from '@angular/material/chips';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar } from '@angular/material/snack-bar';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

import { IncidentService } from '../../services/incident.service';
import { AuthService } from '../../services/auth.service';
import { Incident, IncidentFilters, IncidentStatus, IncidentPriority } from '../../models/incident.model';
import { PageResponse } from '../../models/api-response.model';
import { DateFormatterPipe } from '../../utils/date-formatter.pipe';

@Component({
  selector: 'app-incident-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatToolbarModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatPaginatorModule,
    MatChipsModule,
    MatMenuModule,
    MatDividerModule,
    DateFormatterPipe
  ],
  templateUrl: './incident-list.component.html',
  styleUrls: ['./incident-list.component.scss']
})
export class IncidentListComponent implements OnInit {
  incidents: Incident[] = [];
  pageResponse: PageResponse<Incident> | null = null;
  isLoading = true;
  filterForm: FormGroup;
  
  // Enum values for templates
  statusOptions = Object.values(IncidentStatus);
  priorityOptions = Object.values(IncidentPriority);
  
  // Pagination
  pageSize = 10;
  currentPage = 0;
  totalElements = 0;

  constructor(
    private fb: FormBuilder,
    private incidentService: IncidentService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {
    this.filterForm = this.fb.group({
      q: [''],
      status: [''],
      priority: [''],
      sort: ['createdAt,desc']
    });
  }

  ngOnInit(): void {
    // Initialize filters from query params
    this.route.queryParams.subscribe(params => {
      if (params['status']) this.filterForm.patchValue({ status: params['status'] });
      if (params['priority']) this.filterForm.patchValue({ priority: params['priority'] });
      if (params['q']) this.filterForm.patchValue({ q: params['q'] });
      if (params['page']) this.currentPage = parseInt(params['page']);
      if (params['size']) this.pageSize = parseInt(params['size']);
      
      this.loadIncidents();
    });

    // Setup search debounce
    this.filterForm.get('q')?.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged()
      )
      .subscribe(() => {
        this.currentPage = 0;
        this.loadIncidents();
      });

    // Setup other filter changes
    this.filterForm.get('status')?.valueChanges.subscribe(() => {
      this.currentPage = 0;
      this.loadIncidents();
    });

    this.filterForm.get('priority')?.valueChanges.subscribe(() => {
      this.currentPage = 0;
      this.loadIncidents();
    });

    this.filterForm.get('sort')?.valueChanges.subscribe(() => {
      this.loadIncidents();
    });
  }

  loadIncidents(): void {
    this.isLoading = true;
    
    const filters = this.buildQueryParams();
    
    this.incidentService.getIncidents(filters).subscribe({
      next: (response) => {
        this.pageResponse = response;
        this.incidents = response.content;
        this.totalElements = response.totalElements;
        this.isLoading = false;
        
        // Update URL with current filters
        this.updateUrl(filters);
      },
      error: (error) => {
        this.isLoading = false;
        this.snackBar.open('Failed to load incidents', 'Close', { duration: 5000 });
        console.error('Load incidents error:', error);
      }
    });
  }

  // Build query params from form and pagination (DRY principle)
  buildQueryParams(): IncidentFilters {
    const formValue = this.filterForm.value;
    return {
      q: formValue.q?.trim() || undefined,
      status: formValue.status || undefined,
      priority: formValue.priority || undefined,
      sort: formValue.sort || 'createdAt,desc',
      page: this.currentPage,
      size: this.pageSize
    };
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadIncidents();
  }

  clearFilters(): void {
    this.filterForm.reset({
      q: '',
      status: '',
      priority: '',
      sort: 'createdAt,desc'
    });
    this.currentPage = 0;
    this.loadIncidents();
  }

  navigateToIncident(incident: Incident): void {
    this.router.navigate(['/incidents', incident.id]);
  }

  navigateToNewIncident(): void {
    this.router.navigate(['/incidents/new']);
  }

  navigateToEdit(incident: Incident, event: Event): void {
    event.stopPropagation();
    this.router.navigate(['/incidents', incident.id, 'edit']);
  }

  deleteIncident(incident: Incident, event: Event): void {
    event.stopPropagation();
    
    if (confirm(`Are you sure you want to delete incident "${incident.title}"?`)) {
      this.incidentService.deleteIncident(incident.id!).subscribe({
        next: () => {
          this.snackBar.open('Incident deleted successfully', 'Close', { duration: 3000 });
          this.loadIncidents();
        },
        error: (error) => {
          this.snackBar.open('Failed to delete incident', 'Close', { duration: 5000 });
          console.error('Delete error:', error);
        }
      });
    }
  }

  goToDashboard(): void {
    this.router.navigate(['/dashboard']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'OPEN': return '#f44336';
      case 'IN_PROGRESS': return '#ff9800';
      case 'RESOLVED': return '#4caf50';
      case 'CANCELLED': return '#9e9e9e';
      default: return '#2196f3';
    }
  }

  getPriorityColor(priority: string): string {
    switch (priority) {
      case 'HIGH': return '#f44336';
      case 'MEDIUM': return '#ff9800';
      case 'LOW': return '#4caf50';
      default: return '#2196f3';
    }
  }

  getStatusIcon(status: string): string {
    switch (status) {
      case 'OPEN': return 'error_outline';
      case 'IN_PROGRESS': return 'hourglass_empty';
      case 'RESOLVED': return 'check_circle_outline';
      case 'CANCELLED': return 'cancel';
      default: return 'help_outline';
    }
  }

  private updateUrl(filters: IncidentFilters): void {
    const queryParams: any = {};
    
    if (filters.status) queryParams.status = filters.status;
    if (filters.priority) queryParams.priority = filters.priority;
    if (filters.q) queryParams.q = filters.q;
    if (filters.page && filters.page > 0) queryParams.page = filters.page;
    if (filters.size && filters.size !== 10) queryParams.size = filters.size;
    
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams,
      queryParamsHandling: 'merge'
    });
  }
} 