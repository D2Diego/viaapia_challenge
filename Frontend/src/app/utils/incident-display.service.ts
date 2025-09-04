import { Injectable } from '@angular/core';
import { IncidentStatus, IncidentPriority } from '../models/incident.model';

export interface StatusConfig {
  color: string;
  icon: string;
  displayName: string;
}

export interface PriorityConfig {
  color: string;
  displayName: string;
  order: number;
}

@Injectable({
  providedIn: 'root'
})
export class IncidentDisplayService {
  private readonly statusConfigs: Record<IncidentStatus, StatusConfig> = {
    [IncidentStatus.OPEN]: {
      color: '#f44336',
      icon: 'error_outline',
      displayName: 'Open'
    },
    [IncidentStatus.IN_PROGRESS]: {
      color: '#ff9800',
      icon: 'hourglass_empty',
      displayName: 'In Progress'
    },
    [IncidentStatus.RESOLVED]: {
      color: '#4caf50',
      icon: 'check_circle_outline',
      displayName: 'Resolved'
    },
    [IncidentStatus.CANCELLED]: {
      color: '#9e9e9e',
      icon: 'cancel',
      displayName: 'Cancelled'
    }
  };

  private readonly priorityConfigs: Record<IncidentPriority, PriorityConfig> = {
    [IncidentPriority.HIGH]: {
      color: '#f44336',
      displayName: 'High',
      order: 3
    },
    [IncidentPriority.MEDIUM]: {
      color: '#ff9800',
      displayName: 'Medium',
      order: 2
    },
    [IncidentPriority.LOW]: {
      color: '#4caf50',
      displayName: 'Low',
      order: 1
    }
  };

  private readonly defaultStatusConfig: StatusConfig = {
    color: '#2196f3',
    icon: 'help_outline',
    displayName: 'Unknown'
  };

  private readonly defaultPriorityConfig: PriorityConfig = {
    color: '#2196f3',
    displayName: 'Unknown',
    order: 0
  };

  getStatusConfig(status: IncidentStatus | string): StatusConfig {
    return this.statusConfigs[status as IncidentStatus] || this.defaultStatusConfig;
  }

  getPriorityConfig(priority: IncidentPriority | string): PriorityConfig {
    return this.priorityConfigs[priority as IncidentPriority] || this.defaultPriorityConfig;
  }

  getStatusColor(status: IncidentStatus | string): string {
    return this.getStatusConfig(status).color;
  }

  getStatusIcon(status: IncidentStatus | string): string {
    return this.getStatusConfig(status).icon;
  }

  getStatusDisplayName(status: IncidentStatus | string): string {
    return this.getStatusConfig(status).displayName;
  }

  getPriorityColor(priority: IncidentPriority | string): string {
    return this.getPriorityConfig(priority).color;
  }

  getPriorityDisplayName(priority: IncidentPriority | string): string {
    return this.getPriorityConfig(priority).displayName;
  }

  getAllStatusOptions(): Array<{ value: IncidentStatus; config: StatusConfig }> {
    return Object.entries(this.statusConfigs).map(([value, config]) => ({
      value: value as IncidentStatus,
      config
    }));
  }

  getAllPriorityOptions(): Array<{ value: IncidentPriority; config: PriorityConfig }> {
    return Object.entries(this.priorityConfigs)
      .sort(([, a], [, b]) => b.order - a.order)
      .map(([value, config]) => ({
        value: value as IncidentPriority,
        config
      }));
  }
} 