import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./components/login/login.component').then(c => c.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./components/register/register.component').then(c => c.RegisterComponent)
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./components/dashboard/dashboard.component').then(c => c.DashboardComponent)
  },
  {
    path: 'incidents',
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./components/incident-list/incident-list.component').then(c => c.IncidentListComponent)
      },
      {
        path: 'new',
        loadComponent: () => import('./components/incident-form/incident-form.component').then(c => c.IncidentFormComponent)
      },
      {
        path: ':id',
        loadComponent: () => import('./components/incident-detail/incident-detail.component').then(c => c.IncidentDetailComponent)
      },
      {
        path: ':id/edit',
        loadComponent: () => import('./components/incident-form/incident-form.component').then(c => c.IncidentFormComponent)
      }
    ]
  },
  {
    path: 'users',
    canActivate: [authGuard],
    loadComponent: () => import('./components/user-management/user-management.component').then(c => c.UserManagementComponent)
  },
  {
    path: '**',
    redirectTo: '/dashboard'
  }
];
