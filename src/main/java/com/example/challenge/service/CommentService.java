package com.example.challenge.service;

import com.example.challenge.dto.request.CommentCreateDto;
import com.example.challenge.dto.response.CommentResponseDto;
import com.example.challenge.entity.Comment;
import com.example.challenge.entity.Incident;
import com.example.challenge.repository.CommentRepository;
import com.example.challenge.repository.IncidentRepository;
import com.example.challenge.shared.exception.NotFoundException;
import com.example.challenge.shared.mapper.CommentMapper;
import com.example.challenge.shared.util.AuditUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Transactional
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final IncidentRepository incidentRepository;
    private final CommentMapper commentMapper;
    
    @Autowired
    public CommentService(CommentRepository commentRepository,
                         IncidentRepository incidentRepository,
                         CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.incidentRepository = incidentRepository;
        this.commentMapper = commentMapper;
    }
    

    public CommentResponseDto createComment(UUID incidentId, CommentCreateDto createDto) {

        Incident incident = incidentRepository.findById(incidentId)
            .orElseThrow(() -> new NotFoundException("Incident", incidentId));
        

        Comment comment = commentMapper.toEntity(createDto, incidentId);
        

        Comment savedComment = commentRepository.save(comment);
        

        AuditUtils.touchUpdate(incident);
        incidentRepository.save(incident);
        

        return commentMapper.toResponseDto(savedComment);
    }
    

    @Transactional(readOnly = true)
    public List<CommentResponseDto> findCommentsByIncident(UUID incidentId) {

        if (!incidentRepository.existsById(incidentId)) {
            throw new NotFoundException("Incident", incidentId);
        }
        
        List<Comment> comments = commentRepository.findByIncidentIdOrderByCreatedAtAsc(incidentId);
        
        return comments.stream()
            .map(commentMapper::toResponseDto)
            .collect(Collectors.toList());
    }
    

    @Transactional(readOnly = true)
    public CommentResponseDto findById(UUID id) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Comment", id));
            
        return commentMapper.toResponseDto(comment);
    }
    

    public CommentResponseDto updateComment(UUID id, CommentCreateDto updateDto) {
        Comment existingComment = commentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Comment", id));
        

        commentMapper.updateEntityFromDto(existingComment, updateDto);
        
        Comment savedComment = commentRepository.save(existingComment);
        
        Incident incident = incidentRepository.findById(savedComment.getIncidentId())
            .orElse(null);
        if (incident != null) {
            AuditUtils.touchUpdate(incident);
            incidentRepository.save(incident);
        }
        
        return commentMapper.toResponseDto(savedComment);
    }
    

    public void deleteComment(UUID id) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Comment", id));
        
        UUID incidentId = comment.getIncidentId();
        
        commentRepository.deleteById(id);
        
        Incident incident = incidentRepository.findById(incidentId)
            .orElse(null);
        if (incident != null) {
            AuditUtils.touchUpdate(incident);
            incidentRepository.save(incident);
        }
    }
    

    @Transactional(readOnly = true)
    public long countCommentsByIncident(UUID incidentId) {
        return commentRepository.countByIncidentId(incidentId);
    }
} 