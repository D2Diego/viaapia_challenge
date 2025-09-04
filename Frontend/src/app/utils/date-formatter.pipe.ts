import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'dateFormatter',
  standalone: true
})
export class DateFormatterPipe implements PipeTransform {

  transform(value: string | Date | null | undefined, format: 'short' | 'medium' | 'long' = 'medium'): string {
    if (!value) return '';
    
    const date = typeof value === 'string' ? new Date(value) : value;
    
    if (isNaN(date.getTime())) return '';
    
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
    
    if (diffDays === 0) {
      const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
      if (diffHours === 0) {
        const diffMinutes = Math.floor(diffMs / (1000 * 60));
        return diffMinutes <= 1 ? 'Just now' : `${diffMinutes} minutes ago`;
      }
      return diffHours === 1 ? '1 hour ago' : `${diffHours} hours ago`;
    }
    
    if (diffDays === 1) return 'Yesterday';
    if (diffDays < 7) return `${diffDays} days ago`;
    
    const options: Intl.DateTimeFormatOptions = this.getFormatOptions(format);
    return date.toLocaleDateString('en-US', options);
  }

  private getFormatOptions(format: 'short' | 'medium' | 'long'): Intl.DateTimeFormatOptions {
    switch (format) {
      case 'short':
        return { 
          month: 'numeric', 
          day: 'numeric', 
          year: '2-digit' 
        };
      case 'long':
        return { 
          weekday: 'long',
          year: 'numeric', 
          month: 'long', 
          day: 'numeric',
          hour: '2-digit',
          minute: '2-digit'
        };
      default:
        return { 
          year: 'numeric', 
          month: 'short', 
          day: 'numeric',
          hour: '2-digit',
          minute: '2-digit'
        };
    }
  }
} 