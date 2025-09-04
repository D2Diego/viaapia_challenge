export interface Comment {
  id?: string;
  incidentId: string;
  author: string;
  message: string;
  createdAt?: string;
}

export interface CommentCreateDto {
  author: string;
  message: string;
} 