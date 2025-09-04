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

import { MatSnackBar } from '@angular/material/snack-bar';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

import { IncidentService } from '../../services/incident.service';
import { AuthService } from '../../services/auth.service';
import { Incident, IncidentFilters, IncidentStatus, IncidentPriority } from '../../models/incident.model';
import { PageResponse } from '../../models/api-response.model';
import { DateFormatterPipe } from '../../utils/date-formatter.pipe';
import { IncidentDisplayService } from '../../utils/incident-display.service';
import { FilterService, FilterState } from '../../utils/filter.service';

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
  
  statusOptions = Object.values(IncidentStatus);
  priorityOptions = Object.values(IncidentPriority);
  
  pageSize = 10;
  currentPage = 0;
  totalElements = 0;
  


  constructor(
    private fb: FormBuilder,
    private incidentService: IncidentService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar,
    private displayService: IncidentDisplayService,
    private filterService: FilterService
  ) {
    this.filterForm = this.fb.group(this.filterService.getDefaultFilters());
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const { filters, page, size } = this.filterService.parseRouteParams(params);
      this.filterForm.patchValue(filters);
      this.currentPage = page;
      this.pageSize = size;
      
      this.loadIncidents();
    });

    this.filterForm.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged()
      )
      .subscribe(() => {
        this.currentPage = 0;
        this.loadIncidents();
      });
  }

  loadIncidents(): void {
    this.isLoading = true;
    
    const filterState = this.filterService.normalizeFilterFormValue(this.filterForm.value);
    const filters = this.filterService.buildQueryParams(filterState, this.currentPage, this.pageSize);
    
    this.incidentService.getIncidents(filters).subscribe({
      next: (response) => {
        this.pageResponse = response;
        this.incidents = response.content;
        this.totalElements = response.totalElements;
        this.isLoading = false;
        
        this.updateUrl(filters);
      },
      error: (error) => {
        this.isLoading = false;
        this.snackBar.open('Failed to load incidents', 'Close', { duration: 5000 });
        console.error('Load incidents error:', error);
      }
    });
  }



  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadIncidents();
  }

  clearFilters(): void {
    const defaultFilters = this.filterService.getDefaultFilters();
    this.filterForm.reset(defaultFilters);
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
    return this.displayService.getStatusColor(status);
  }

  getPriorityColor(priority: string): string {
    return this.displayService.getPriorityColor(priority);
  }

  getStatusIcon(status: string): string {
    return this.displayService.getStatusIcon(status);
  }

  getStatusDisplayName(status: string): string {
    return this.displayService.getStatusDisplayName(status);
  }

  getPriorityDisplayName(priority: string): string {
    return this.displayService.getPriorityDisplayName(priority);
  }

  private updateUrl(filters: IncidentFilters): void {
    const queryParams = this.filterService.createUrlParams(filters);
    
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams,
      queryParamsHandling: 'merge'
    });
  }

  get sortOptions() {
    return this.filterService.sortOptions;
  }

  hasActiveFilters(): boolean {
    const filterState = this.filterService.normalizeFilterFormValue(this.filterForm.value);
    return this.filterService.hasActiveFilters(filterState);
  }

  getActiveFiltersCount(): number {
    let count = 0;
    const formValue = this.filterForm.value;
    
    if (formValue.q?.trim()) count++;
    if (formValue.status) count++;
    if (formValue.priority) count++;
    
    return count;
  }

  getSortIcon(field: string, direction: 'asc' | 'desc'): string {
    const iconMap: { [key: string]: string } = {
      createdAt: direction === 'desc' ? 'schedule' : 'schedule',
      title: direction === 'desc' ? 'sort_by_alpha' : 'sort_by_alpha',
      priority: direction === 'desc' ? 'priority_high' : 'priority_high',
      status: direction === 'desc' ? 'swap_vert' : 'swap_vert',
      updatedAt: direction === 'desc' ? 'update' : 'update'
    };
    
    return iconMap[field] || 'sort';
  }


} 