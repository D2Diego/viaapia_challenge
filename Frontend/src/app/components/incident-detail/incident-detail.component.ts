import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar } from '@angular/material/snack-bar';

import { IncidentService } from '../../services/incident.service';
import { CommentService } from '../../services/comment.service';
import { AuthService } from '../../services/auth.service';
import { Incident, IncidentStatus, StatusUpdateDto } from '../../models/incident.model';
import { Comment, CommentCreateDto } from '../../models/comment.model';
import { DateFormatterPipe } from '../../utils/date-formatter.pipe';
import { IncidentDisplayService } from '../../utils/incident-display.service';

@Component({
  selector: 'app-incident-detail',
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
    MatChipsModule,
    MatMenuModule,
    MatDividerModule,
    DateFormatterPipe
  ],
  template: `
    <mat-toolbar color="primary">
      <button mat-icon-button (click)="goBack()">
        <mat-icon>arrow_back</mat-icon>
      </button>
      <span>Incident Details</span>
      <span class="spacer"></span>
      <button mat-icon-button [matMenuTriggerFor]="menu" *ngIf="incident">
        <mat-icon>more_vert</mat-icon>
      </button>
      <mat-menu #menu="matMenu">
        <button mat-menu-item (click)="editIncident()">
          <mat-icon>edit</mat-icon>
          <span>Edit Incident</span>
        </button>
        <button mat-menu-item (click)="deleteIncident()">
          <mat-icon>delete</mat-icon>
          <span>Delete Incident</span>
        </button>
      </mat-menu>
    </mat-toolbar>

    <div class="detail-container" *ngIf="!isLoading && incident">
      <!-- Incident Details Card -->
      <mat-card class="incident-card">
        <mat-card-header>
          <mat-card-title>{{incident.title}}</mat-card-title>
          <mat-card-subtitle>
            Created {{incident.createdAt | dateFormatter:'medium'}}
            <span *ngIf="incident.updatedAt !== incident.createdAt">
              • Updated {{incident.updatedAt | dateFormatter:'medium'}}
            </span>
          </mat-card-subtitle>
        </mat-card-header>
        
        <mat-card-content>
          <div class="incident-info">
            <div class="info-row">
              <div class="info-item">
                <strong>Status:</strong>
                <mat-chip [style.background-color]="getStatusColor(incident.status)" class="status-chip">
                  <mat-icon>{{getStatusIcon(incident.status)}}</mat-icon>
                  {{incident.status.replace('_', ' ')}}
                </mat-chip>
              </div>
              
              <div class="info-item">
                <strong>Priority:</strong>
                <mat-chip [style.background-color]="getPriorityColor(incident.priority)" class="priority-chip">
                  {{incident.priority}}
                </mat-chip>
              </div>
            </div>
            
            <div class="info-row">
              <div class="info-item">
                <strong>Responsible:</strong>
                <span>{{incident.responsibleEmail}}</span>
              </div>
            </div>
            
            <div class="info-row" *ngIf="incident.tags && incident.tags.length > 0">
              <div class="info-item">
                <strong>Tags:</strong>
                <div class="tags-container">
                  <mat-chip *ngFor="let tag of incident.tags">{{tag}}</mat-chip>
                </div>
              </div>
            </div>
            
            <div class="description-section" *ngIf="incident.description">
              <strong>Description:</strong>
              <p class="description-text">{{incident.description}}</p>
            </div>
          </div>
        </mat-card-content>
        
        <mat-card-actions>
          <div class="status-actions">
            <strong>Change Status:</strong>
            <button mat-button 
                    *ngFor="let status of statusOptions" 
                    [disabled]="status === incident.status || isUpdatingStatus"
                    (click)="updateStatus(status)"
                    [style.color]="getStatusColor(status)">
              {{status.replace('_', ' ')}}
            </button>
          </div>
        </mat-card-actions>
      </mat-card>

      <!-- Comments Section -->
      <mat-card class="comments-card">
        <mat-card-header>
          <mat-card-title>Comments ({{comments.length}})</mat-card-title>
        </mat-card-header>
        
        <mat-card-content>
          <!-- Add Comment Form -->
          <form [formGroup]="commentForm" (ngSubmit)="addComment()" class="comment-form">
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Author (optional)</mat-label>
              <input matInput formControlName="author" placeholder="Leave blank for anonymous">
              <mat-hint>Leave empty to post anonymously</mat-hint>
            </mat-form-field>
            
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Message</mat-label>
              <textarea matInput rows="3" formControlName="message" placeholder="Add a comment..."></textarea>
              <mat-error *ngIf="message?.hasError('required')">Message is required</mat-error>
              <mat-error *ngIf="message?.hasError('maxlength')">Message cannot exceed 2000 characters</mat-error>
            </mat-form-field>
            
            <button mat-raised-button color="primary" type="submit" 
                    [disabled]="!message?.value?.trim() || isAddingComment">
              <mat-spinner *ngIf="isAddingComment" diameter="20"></mat-spinner>
              {{isAddingComment ? 'Adding...' : 'Add Comment'}}
            </button>
          </form>
          
          <mat-divider class="comment-divider"></mat-divider>
          
          <!-- Comments List -->
          <div class="comments-list">
            <div *ngFor="let comment of comments" class="comment-item">
              <div class="comment-header">
                <strong>{{comment.author}}</strong>
                <span class="comment-date">{{comment.createdAt | dateFormatter:'medium'}}</span>
              </div>
              <p class="comment-message">{{comment.message}}</p>
            </div>
            
            <div *ngIf="comments.length === 0" class="no-comments">
              <mat-icon>comment</mat-icon>
              <p>No comments yet. Be the first to add one!</p>
            </div>
          </div>
        </mat-card-content>
      </mat-card>
    </div>

    <div *ngIf="isLoading" class="loading-container">
      <mat-spinner></mat-spinner>
      <p>Loading incident details...</p>
    </div>
  `,
  styles: [`
    .spacer { flex: 1 1 auto; }
    .detail-container { padding: 24px; max-width: 1000px; margin: 0 auto; }
    .incident-card, .comments-card { margin-bottom: 24px; }
    .incident-info { display: flex; flex-direction: column; gap: 16px; }
    .info-row { display: flex; gap: 32px; flex-wrap: wrap; }
    .info-item { display: flex; flex-direction: column; gap: 8px; }
    .status-chip, .priority-chip { color: white; font-weight: 500; }
    .tags-container { display: flex; gap: 8px; flex-wrap: wrap; }
    .description-section { margin-top: 16px; }
    .description-text { margin: 8px 0; white-space: pre-wrap; }
    .status-actions { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }
    .comment-form { display: flex; flex-direction: column; gap: 16px; margin-bottom: 24px; }
    .full-width { width: 100%; }
    .comment-divider { margin: 24px 0; }
    .comments-list { display: flex; flex-direction: column; gap: 16px; }
    .comment-item { padding: 16px; background-color: #f5f5f5; border-radius: 8px; }
    .comment-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
    .comment-date { color: #666; font-size: 14px; }
    .comment-message { margin: 0; white-space: pre-wrap; }
    .no-comments { text-align: center; padding: 32px; color: #666; }
    .loading-container { display: flex; flex-direction: column; align-items: center; padding: 48px; }
  `]
})
export class IncidentDetailComponent implements OnInit {
  incident: Incident | null = null;
  comments: Comment[] = [];
  commentForm: FormGroup;
  incidentId: string | null = null;
  isLoading = true;
  isAddingComment = false;
  isUpdatingStatus = false;
  
  statusOptions = Object.values(IncidentStatus);

  constructor(
    private fb: FormBuilder,
    private incidentService: IncidentService,
    private commentService: CommentService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar,
    private displayService: IncidentDisplayService
  ) {
    this.commentForm = this.fb.group({
      author: [''],
      message: ['', [Validators.required, Validators.maxLength(2000)]]
    });
  }

  ngOnInit(): void {
    this.incidentId = this.route.snapshot.paramMap.get('id');
    if (this.incidentId) {
      this.loadIncident();
      this.loadComments();
    }

    // Set default author from current user
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.commentForm.patchValue({ author: user.username });
      }
    });
  }

  loadIncident(): void {
    if (!this.incidentId) return;
    
    this.incidentService.getIncidentById(this.incidentId).subscribe({
      next: (incident) => {
        this.incident = incident;
        this.isLoading = false;
      },
      error: (error) => {
        this.isLoading = false;
        this.snackBar.open('Failed to load incident', 'Close', { duration: 5000 });
        this.goBack();
      }
    });
  }

  loadComments(): void {
    if (!this.incidentId) return;
    
    this.commentService.getCommentsByIncident(this.incidentId).subscribe({
      next: (comments) => {
        this.comments = comments;
      },
      error: (error) => {
        console.error('Failed to load comments:', error);
      }
    });
  }

  addComment(): void {
    const messageValue = this.message?.value?.trim();
    if (messageValue && !this.isAddingComment && this.incidentId) {
      this.isAddingComment = true;
      
              const comment: CommentCreateDto = {
          author: this.author?.value?.trim() || 'Anonymous',
          message: messageValue
        };
      
      this.commentService.createComment(this.incidentId, comment).subscribe({
        next: (newComment) => {
          this.comments.unshift(newComment);
          this.commentForm.get('message')?.setValue('');
          this.isAddingComment = false;
          this.snackBar.open('Comment added successfully', 'Close', { duration: 3000 });
        },
        error: (error) => {
          this.isAddingComment = false;
          this.snackBar.open('Failed to add comment', 'Close', { duration: 5000 });
        }
      });
    }
  }

  updateStatus(status: IncidentStatus): void {
    if (!this.incident || this.isUpdatingStatus) return;
    
    this.isUpdatingStatus = true;
    const statusUpdate: StatusUpdateDto = { status };
    
    this.incidentService.updateIncidentStatus(this.incident.id!, statusUpdate).subscribe({
      next: (updatedIncident) => {
        this.incident = updatedIncident;
        this.isUpdatingStatus = false;
        this.snackBar.open('Status updated successfully', 'Close', { duration: 3000 });
      },
      error: (error) => {
        this.isUpdatingStatus = false;
        this.snackBar.open('Failed to update status', 'Close', { duration: 5000 });
      }
    });
  }

  editIncident(): void {
    if (this.incident) {
      this.router.navigate(['/incidents', this.incident.id, 'edit']);
    }
  }

  deleteIncident(): void {
    if (!this.incident) return;
    
    if (confirm(`Are you sure you want to delete incident "${this.incident.title}"?`)) {
      this.incidentService.deleteIncident(this.incident.id!).subscribe({
        next: () => {
          this.snackBar.open('Incident deleted successfully', 'Close', { duration: 3000 });
          this.router.navigate(['/incidents']);
        },
        error: (error) => {
          this.snackBar.open('Failed to delete incident', 'Close', { duration: 5000 });
        }
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/incidents']);
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

  get author() { return this.commentForm.get('author'); }
  get message() { return this.commentForm.get('message'); }
} 