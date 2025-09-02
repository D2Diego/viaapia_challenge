package com.example.challenge.shared.mapper;

import com.example.challenge.dto.request.CommentCreateDto;
import com.example.challenge.dto.response.CommentResponseDto;
import com.example.challenge.entity.Comment;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class CommentMapper {
    

    public CommentResponseDto toResponseDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        
        return new CommentResponseDto(
            comment.getId(),
            comment.getIncidentId(),
            comment.getAuthor(),
            comment.getMessage(),
            comment.getCreatedAt()
        );
    }
    

    public Comment toEntity(CommentCreateDto createDto, UUID incidentId) {
        if (createDto == null) {
            return null;
        }
        
        Comment comment = new Comment();
        comment.setAuthor(createDto.getAuthor());
        comment.setMessage(createDto.getMessage());
        comment.setIncidentId(incidentId);
        
        return comment;
    }
    

    public void updateEntityFromDto(Comment comment, CommentCreateDto updateDto) {
        if (comment == null || updateDto == null) {
            return;
        }
        
        if (updateDto.getAuthor() != null) {
            comment.setAuthor(updateDto.getAuthor());
        }
        
        if (updateDto.getMessage() != null) {
            comment.setMessage(updateDto.getMessage());
        }
    }
} 