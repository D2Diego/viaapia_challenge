import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatMenuModule } from '@angular/material/menu';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';

import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';
import { User, UserCreateDto } from '../../models/user.model';
import { PageResponse } from '../../models/api-response.model';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatToolbarModule,
    MatTableModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDialogModule,
    MatMenuModule,
    MatChipsModule,
    MatDividerModule
  ],
  template: `
    <mat-toolbar color="primary">
      <button mat-icon-button (click)="goToDashboard()">
        <mat-icon>dashboard</mat-icon>
      </button>
      <span>User Management</span>
      <span class="spacer"></span>
      <button mat-raised-button color="accent" (click)="openCreateUserDialog()">
        <mat-icon>person_add</mat-icon>
        Add User
      </button>
      <button mat-icon-button [matMenuTriggerFor]="menu">
        <mat-icon>more_vert</mat-icon>
      </button>
      <mat-menu #menu="matMenu">
        <button mat-menu-item (click)="goToDashboard()">
          <mat-icon>dashboard</mat-icon>
          <span>Dashboard</span>
        </button>
        <button mat-menu-item (click)="navigateToIncidents()">
          <mat-icon>assignment</mat-icon>
          <span>Incidents</span>
        </button>
        <mat-divider></mat-divider>
        <button mat-menu-item (click)="logout()">
          <mat-icon>logout</mat-icon>
          <span>Logout</span>
        </button>
      </mat-menu>
    </mat-toolbar>

    <div class="user-management-container">
      <!-- Users Table -->
      <mat-card class="users-card">
        <mat-card-header>
          <mat-card-title>
            <mat-icon>people</mat-icon>
            System Users
            <mat-chip *ngIf="pageResponse" color="accent">
              {{pageResponse.totalElements}} total
            </mat-chip>
          </mat-card-title>
        </mat-card-header>
        
        <mat-card-content *ngIf="!isLoading">
          <div class="table-container">
            <table mat-table [dataSource]="users" class="users-table">
              <!-- Username Column -->
              <ng-container matColumnDef="username">
                <th mat-header-cell *matHeaderCellDef>Username</th>
                <td mat-cell *matCellDef="let user">
                  <div class="user-info">
                    <mat-icon>person</mat-icon>
                    <span>{{user.username}}</span>
                  </div>
                </td>
              </ng-container>

                             <!-- Role Column -->
               <ng-container matColumnDef="role">
                 <th mat-header-cell *matHeaderCellDef>Roles</th>
                 <td mat-cell *matCellDef="let user">
                   <div class="roles-container">
                     <mat-chip *ngFor="let role of user.roles" [color]="getRoleColor(role.name)">
                       {{role.name}}
                     </mat-chip>
                     <span *ngIf="!user.roles || user.roles.length === 0" class="no-roles">No roles</span>
                   </div>
                 </td>
               </ng-container>

                             <!-- Username Display Column -->
               <ng-container matColumnDef="createdAt">
                 <th mat-header-cell *matHeaderCellDef>Info</th>
                 <td mat-cell *matCellDef="let user">
                   <div class="user-details">
                     <span class="user-id">ID: {{user.userId || 'N/A'}}</span>
                   </div>
                 </td>
               </ng-container>

              <!-- Actions Column -->
              <ng-container matColumnDef="actions">
                <th mat-header-cell *matHeaderCellDef>Actions</th>
                <td mat-cell *matCellDef="let user">
                  <button mat-icon-button [matMenuTriggerFor]="userMenu">
                    <mat-icon>more_vert</mat-icon>
                  </button>
                  <mat-menu #userMenu="matMenu">
                    <button mat-menu-item (click)="editUser(user)">
                      <mat-icon>edit</mat-icon>
                      <span>Edit</span>
                    </button>
                                         <button mat-menu-item (click)="deleteUser(user)" [disabled]="user.userId === currentUser?.userId">
                      <mat-icon>delete</mat-icon>
                      <span>Delete</span>
                    </button>
                  </mat-menu>
                </td>
              </ng-container>

              <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
              <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
            </table>

            <!-- No Users State -->
            <div *ngIf="users.length === 0" class="no-users">
              <mat-icon>people_outline</mat-icon>
              <h3>No users found</h3>
              <p>Start by creating your first user.</p>
              <button mat-raised-button color="primary" (click)="openCreateUserDialog()">
                <mat-icon>person_add</mat-icon>
                Create User
              </button>
            </div>
          </div>
        </mat-card-content>

        <div *ngIf="isLoading" class="loading-container">
          <mat-spinner></mat-spinner>
          <p>Loading users...</p>
        </div>
      </mat-card>

      <!-- Pagination -->
      <mat-paginator 
        *ngIf="!isLoading && pageResponse && pageResponse.totalElements > 0"
        [length]="totalElements"
        [pageSize]="pageSize"
        [pageIndex]="currentPage"
        [pageSizeOptions]="[5, 10, 25, 50]"
        (page)="onPageChange($event)"
        showFirstLastButtons>
      </mat-paginator>
    </div>
  `,
  styles: [`
    .spacer { flex: 1 1 auto; }
    
    .user-management-container {
      padding: 24px;
      max-width: 1200px;
      margin: 0 auto;
    }
    
    .users-card {
      margin-bottom: 24px;
      
      .mat-card-header {
        .mat-card-title {
          display: flex;
          align-items: center;
          gap: 12px;
          
          mat-icon {
            color: var(--primary-500);
          }
          
          mat-chip {
            font-size: 12px;
            height: 24px;
            min-height: 24px;
          }
        }
      }
    }
    
    .table-container {
      overflow-x: auto;
    }
    
    .users-table {
      width: 100%;
      
      .user-info {
        display: flex;
        align-items: center;
        gap: 8px;
        
        mat-icon {
          font-size: 18px;
          width: 18px;
          height: 18px;
          color: var(--text-secondary);
        }
      }
      
             mat-chip {
         font-size: 12px;
         font-weight: 500;
       }
       
       .roles-container {
         display: flex;
         flex-wrap: wrap;
         gap: 4px;
         align-items: center;
         
         .no-roles {
           font-size: 12px;
           color: var(--text-tertiary);
           font-style: italic;
         }
       }
       
       .user-details {
         .user-id {
           font-size: 12px;
           color: var(--text-secondary);
           font-family: monospace;
         }
       }
     }
    
    .loading-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 48px;
      
      p {
        margin-top: 16px;
        color: var(--text-secondary);
      }
    }
    
    .no-users {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 48px;
      text-align: center;
      
      mat-icon {
        font-size: 64px;
        width: 64px;
        height: 64px;
        color: var(--text-tertiary);
        margin-bottom: 16px;
      }
      
      h3 {
        margin: 0 0 8px;
        color: var(--text-primary);
      }
      
      p {
        margin: 0 0 24px;
        color: var(--text-secondary);
      }
    }
  `]
})
export class UserManagementComponent implements OnInit {
  users: User[] = [];
  pageResponse: PageResponse<User> | null = null;
  currentUser: User | null = null;
  isLoading = true;
  
  displayedColumns: string[] = ['username', 'role', 'createdAt', 'actions'];
  
  pageSize = 10;
  currentPage = 0;
  totalElements = 0;

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadUsers();
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

  loadUsers(): void {
    this.isLoading = true;
    
    this.userService.getUsers(this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        this.pageResponse = response;
        this.users = response.content;
        this.totalElements = response.totalElements;
        this.isLoading = false;
      },
      error: (error) => {
        this.isLoading = false;
        this.snackBar.open('Failed to load users', 'Close', { duration: 5000 });
        console.error('Load users error:', error);
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadUsers();
  }

  openCreateUserDialog(): void {
    this.snackBar.open('User creation dialog - To be implemented', 'Close', { duration: 3000 });
  }

  editUser(user: User): void {
    this.snackBar.open(`Edit user ${user.username} - To be implemented`, 'Close', { duration: 3000 });
  }

  deleteUser(user: User): void {
    if (user.userId === this.currentUser?.userId) {
      this.snackBar.open('Cannot delete your own account', 'Close', { duration: 3000 });
      return;
    }

    if (confirm(`Are you sure you want to delete user "${user.username}"?`)) {
      this.userService.deleteUser(user.userId!).subscribe({
        next: () => {
          this.snackBar.open('User deleted successfully', 'Close', { duration: 3000 });
          this.loadUsers();
        },
        error: (error) => {
          this.snackBar.open('Failed to delete user', 'Close', { duration: 5000 });
          console.error('Delete user error:', error);
        }
      });
    }
  }

  getRoleColor(role: string): 'primary' | 'accent' | 'warn' {
    switch (role?.toUpperCase()) {
      case 'ADMIN': return 'warn';
      case 'USER': return 'primary';
      default: return 'accent';
    }
  }

  goToDashboard(): void {
    this.router.navigate(['/dashboard']);
  }

  navigateToIncidents(): void {
    this.router.navigate(['/incidents']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
} 