package com.example.challenge.comment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.challenge.incedent.Incident;
import com.example.challenge.incedent.IncidentRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/incidents")
public class CommentController {
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private IncidentRepository incidentRepository;
    
    /**
     * POST /incidents/{id}/comments - Criar comentário para um incidente
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable UUID id, @Valid @RequestBody Comment comment) {
        try {
            // Verificar se o incidente existe
            Optional<Incident> incident = incidentRepository.findById(id);
            if (!incident.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Configurar o comentário
            comment.setId(null);
            comment.setIncidentId(id);
            
            Comment savedComment = commentRepository.save(comment);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * GET /incidents/{id}/comments - Listar comentários de um incidente
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<Comment>> getCommentsByIncident(@PathVariable UUID id) {
        try {
            // Verificar se o incidente existe
            Optional<Incident> incident = incidentRepository.findById(id);
            if (!incident.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            List<Comment> comments = commentRepository.findByIncidentIdOrderByCreatedAtAsc(id);
            return ResponseEntity.ok(comments);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
