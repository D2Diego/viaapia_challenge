import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiClientService } from './api-client.service';
import { Comment, CommentCreateDto } from '../models/comment.model';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  constructor(private apiClient: ApiClientService) {}

  getCommentsByIncident(incidentId: string): Observable<Comment[]> {
    return this.apiClient.getList<Comment>(`/incidents/${incidentId}/comments`);
  }

  createComment(incidentId: string, comment: CommentCreateDto): Observable<Comment> {
    const normalizedComment = this.normalizeCommentFormValue(comment);
    return this.apiClient.post<CommentCreateDto, Comment>(`/incidents/${incidentId}/comments`, normalizedComment);
  }

  getCommentById(commentId: string): Observable<Comment> {
    return this.apiClient.getOne<Comment>(`/comments`, commentId);
  }

  deleteComment(commentId: string): Observable<void> {
    return this.apiClient.delete('/comments', commentId);
  }

  private normalizeCommentFormValue(formValue: CommentCreateDto): CommentCreateDto {
    return {
      author: formValue.author?.trim() || '',
      message: formValue.message?.trim() || ''
    };
  }
} 