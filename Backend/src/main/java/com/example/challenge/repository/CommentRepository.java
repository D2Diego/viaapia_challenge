package com.example.challenge.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.challenge.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    
    List<Comment> findByIncidentIdOrderByCreatedAtAsc(UUID incidentId);
    
    long countByIncidentId(UUID incidentId);
}
 