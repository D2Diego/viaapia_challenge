import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar } from '@angular/material/snack-bar';

import { IncidentService } from '../../services/incident.service';
import { AuthService } from '../../services/auth.service';
import { IncidentDisplayService } from '../../utils/incident-display.service';
import { StatsResponse } from '../../models/api-response.model';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatToolbarModule,
    MatMenuModule,
    MatDividerModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  stats: StatsResponse | null = null;
  currentUser: User | null = null;
  isLoading = true;

  constructor(
    private incidentService: IncidentService,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar,
    private displayService: IncidentDisplayService
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

  loadDashboardData(): void {
    this.isLoading = true;
    
    this.incidentService.getStats().subscribe({
      next: (stats) => {
        this.stats = stats;
        this.isLoading = false;
      },
      error: (error) => {
        this.isLoading = false;
        this.snackBar.open('Failed to load dashboard data', 'Close', { duration: 5000 });
        console.error('Dashboard error:', error);
      }
    });
  }

  navigateToIncidents(status?: string): void {
    const queryParams = status ? { status } : {};
    this.router.navigate(['/incidents'], { queryParams });
  }

  navigateToNewIncident(): void {
    this.router.navigate(['/incidents/new']);
  }

  navigateToUsers(): void {
    this.router.navigate(['/users']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  getStatusIcon(status: string): string {
    return this.displayService.getStatusIcon(status.toUpperCase());
  }

  getPriorityIcon(priority: string): string {
    const priorityUpper = priority.toUpperCase();
    switch (priorityUpper) {
      case 'HIGH': return 'priority_high';
      case 'MEDIUM': return 'remove';
      case 'LOW': return 'expand_more';
      default: return 'help_outline';
    }
  }

  getStatusColor(status: string): string {
    return this.displayService.getStatusColor(status.toUpperCase());
  }

  getPriorityColor(priority: string): string {
    return this.displayService.getPriorityColor(priority.toUpperCase());
  }
} 