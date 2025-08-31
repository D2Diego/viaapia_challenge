package com.example.challenge.incedent;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, UUID> {
    
    // Métodos simples usando Spring Data JPA
    List<Incident> findByStatus(Status status);
    List<Incident> findByPriority(Priority priority);
    List<Incident> findByTitleContainingIgnoreCase(String title);
    List<Incident> findByDescriptionContainingIgnoreCase(String description);
}