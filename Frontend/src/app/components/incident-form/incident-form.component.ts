import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormArray } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSnackBar } from '@angular/material/snack-bar';

import { IncidentService } from '../../services/incident.service';
import { IncidentCreateDto, IncidentUpdateDto, IncidentStatus, IncidentPriority } from '../../models/incident.model';

@Component({
  selector: 'app-incident-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatChipsModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatToolbarModule
  ],
  template: `
    <mat-toolbar color="primary">
      <button mat-icon-button (click)="goBack()">
        <mat-icon>arrow_back</mat-icon>
      </button>
      <span>{{isEditMode ? 'Edit Incident' : 'Create New Incident'}}</span>
    </mat-toolbar>

    <div class="form-container">
      <mat-card>
        <mat-card-header>
          <mat-card-title>{{isEditMode ? 'Edit Incident' : 'Create New Incident'}}</mat-card-title>
        </mat-card-header>
        
        <mat-card-content *ngIf="!isLoading">
          <form [formGroup]="incidentForm" (ngSubmit)="onSubmit()" class="incident-form">
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Title</mat-label>
              <input matInput formControlName="title" placeholder="Enter incident title">
              <mat-error *ngIf="title?.hasError('required')">Title is required</mat-error>
              <mat-error *ngIf="title?.hasError('minlength')">Title must be at least 5 characters</mat-error>
              <mat-error *ngIf="title?.hasError('maxlength')">Title cannot exceed 120 characters</mat-error>
            </mat-form-field>

            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Description</mat-label>
              <textarea matInput rows="4" formControlName="description" placeholder="Enter incident description"></textarea>
              <mat-error *ngIf="description?.hasError('maxlength')">Description cannot exceed 5000 characters</mat-error>
            </mat-form-field>

            <div class="form-row">
              <mat-form-field appearance="outline" class="half-width">
                <mat-label>Priority</mat-label>
                <mat-select formControlName="priority">
                  <mat-option *ngFor="let priority of priorityOptions" [value]="priority">
                    {{priority}}
                  </mat-option>
                </mat-select>
                <mat-error *ngIf="priority?.hasError('required')">Priority is required</mat-error>
              </mat-form-field>

              <mat-form-field appearance="outline" class="half-width">
                <mat-label>Status</mat-label>
                <mat-select formControlName="status">
                  <mat-option *ngFor="let status of statusOptions" [value]="status">
                    {{status.replace('_', ' ')}}
                  </mat-option>
                </mat-select>
                <mat-error *ngIf="status?.hasError('required')">Status is required</mat-error>
              </mat-form-field>
            </div>

            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Responsible Email</mat-label>
              <input matInput type="email" formControlName="responsibleEmail" placeholder="Enter responsible person's email">
              <mat-error *ngIf="responsibleEmail?.hasError('required')">Responsible email is required</mat-error>
              <mat-error *ngIf="responsibleEmail?.hasError('email')">Please enter a valid email</mat-error>
            </mat-form-field>

            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Tags</mat-label>
              <input matInput formControlName="tagsInput" placeholder="Enter tags separated by commas" (keyup.enter)="addTag()">
              <mat-hint>Press Enter or comma to add tags</mat-hint>
            </mat-form-field>

            <div class="tags-container" *ngIf="tags.length > 0">
              <mat-chip-listbox>
                <mat-chip-option *ngFor="let tag of tags; let i = index" (removed)="removeTag(i)">
                  {{tag}}
                  <button matChipRemove>
                    <mat-icon>cancel</mat-icon>
                  </button>
                </mat-chip-option>
              </mat-chip-listbox>
            </div>

            <div class="form-actions">
              <button mat-button type="button" (click)="goBack()" [disabled]="isSubmitting">
                Cancel
              </button>
              <button mat-raised-button color="primary" type="submit" [disabled]="!incidentForm.valid || isSubmitting">
                <mat-spinner *ngIf="isSubmitting" diameter="20"></mat-spinner>
                {{isSubmitting ? 'Saving...' : (isEditMode ? 'Update' : 'Create')}}
              </button>
            </div>
          </form>
        </mat-card-content>

        <div *ngIf="isLoading" class="loading-container">
          <mat-spinner></mat-spinner>
          <p>Loading incident data...</p>
        </div>
      </mat-card>
    </div>
  `,
  styles: [`
    .form-container { padding: 24px; max-width: 800px; margin: 0 auto; }
    .incident-form { display: flex; flex-direction: column; gap: 16px; }
    .full-width { width: 100%; }
    .form-row { display: flex; gap: 16px; }
    .half-width { flex: 1; }
    .tags-container { margin: 16px 0; }
    .form-actions { display: flex; justify-content: flex-end; gap: 16px; margin-top: 24px; }
    .loading-container { display: flex; flex-direction: column; align-items: center; padding: 48px; }
  `]
})
export class IncidentFormComponent implements OnInit {
  incidentForm: FormGroup;
  isEditMode = false;
  isLoading = false;
  isSubmitting = false;
  incidentId: string | null = null;
  tags: string[] = [];
  
  statusOptions = Object.values(IncidentStatus);
  priorityOptions = Object.values(IncidentPriority);

  constructor(
    private fb: FormBuilder,
    private incidentService: IncidentService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {
    this.incidentForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(120)]],
      description: ['', [Validators.maxLength(5000)]],
      priority: ['', Validators.required],
      status: ['', Validators.required],
      responsibleEmail: ['', [Validators.required, Validators.email]],
      tagsInput: ['']
    });
  }

  ngOnInit(): void {
    this.incidentId = this.route.snapshot.paramMap.get('id');
    this.isEditMode = !!this.incidentId;
    
    if (this.isEditMode) {
      this.loadIncident();
    } else {
      this.incidentForm.patchValue({
        priority: IncidentPriority.MEDIUM,
        status: IncidentStatus.OPEN
      });
    }

    this.incidentForm.get('tagsInput')?.valueChanges.subscribe(value => {
      if (value && (value.includes(',') || value.includes(';'))) {
        this.addTag();
      }
    });
  }

  loadIncident(): void {
    if (!this.incidentId) return;
    
    this.isLoading = true;
    this.incidentService.getIncidentById(this.incidentId).subscribe({
      next: (incident) => {
        this.incidentForm.patchValue(incident);
        this.tags = incident.tags || [];
        this.isLoading = false;
      },
      error: (error) => {
        this.isLoading = false;
        this.snackBar.open('Failed to load incident', 'Close', { duration: 5000 });
        this.goBack();
      }
    });
  }

  onSubmit(): void {
    if (this.incidentForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;
      
      const formData = { ...this.incidentForm.value };
      delete formData.tagsInput;
      formData.tags = this.tags;

      const operation = this.isEditMode 
        ? this.incidentService.updateIncident(this.incidentId!, formData as IncidentUpdateDto)
        : this.incidentService.createIncident(formData as IncidentCreateDto);

      operation.subscribe({
        next: (incident) => {
          this.isSubmitting = false;
          const message = this.isEditMode ? 'Incident updated successfully' : 'Incident created successfully';
          this.snackBar.open(message, 'Close', { duration: 3000 });
          this.router.navigate(['/incidents', incident.id]);
        },
        error: (error) => {
          this.isSubmitting = false;
          this.snackBar.open(error.message || 'Failed to save incident', 'Close', { duration: 5000 });
        }
      });
    }
  }

  addTag(): void {
    const input = this.incidentForm.get('tagsInput');
    const value = input?.value?.trim();
    
    if (value) {
      const newTags = value.split(/[,;]/).map((tag: string) => tag.trim()).filter((tag: string) => tag);
      newTags.forEach((tag: string) => {
        if (!this.tags.includes(tag)) {
          this.tags.push(tag);
        }
      });
      input?.setValue('');
    }
  }

  removeTag(index: number): void {
    this.tags.splice(index, 1);
  }

  goBack(): void {
    if (this.isEditMode) {
      this.router.navigate(['/incidents', this.incidentId]);
    } else {
      this.router.navigate(['/incidents']);
    }
  }

  get title() { return this.incidentForm.get('title'); }
  get description() { return this.incidentForm.get('description'); }
  get priority() { return this.incidentForm.get('priority'); }
  get status() { return this.incidentForm.get('status'); }
  get responsibleEmail() { return this.incidentForm.get('responsibleEmail'); }
} 