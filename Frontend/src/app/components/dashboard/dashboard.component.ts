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
    private snackBar: MatSnackBar
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

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  getStatusIcon(status: string): string {
    switch (status.toLowerCase()) {
      case 'open': return 'error_outline';
      case 'in_progress': return 'hourglass_empty';
      case 'resolved': return 'check_circle_outline';
      case 'cancelled': return 'cancel';
      default: return 'help_outline';
    }
  }

  getPriorityIcon(priority: string): string {
    switch (priority.toLowerCase()) {
      case 'high': return 'priority_high';
      case 'medium': return 'remove';
      case 'low': return 'expand_more';
      default: return 'help_outline';
    }
  }

  getStatusColor(status: string): string {
    switch (status.toLowerCase()) {
      case 'open': return '#f44336';
      case 'in_progress': return '#ff9800';
      case 'resolved': return '#4caf50';
      case 'cancelled': return '#9e9e9e';
      default: return '#2196f3';
    }
  }

  getPriorityColor(priority: string): string {
    switch (priority.toLowerCase()) {
      case 'high': return '#f44336';
      case 'medium': return '#ff9800';
      case 'low': return '#4caf50';
      default: return '#2196f3';
    }
  }
} 