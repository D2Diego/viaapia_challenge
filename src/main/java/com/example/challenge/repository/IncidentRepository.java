package com.example.challenge.repository;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.challenge.entity.Incident;
import com.example.challenge.entity.IncidentPriority;
import com.example.challenge.entity.Status;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, UUID> {
    
    // Métodos simples usando Spring Data JPA
    List<Incident> findByStatus(Status status);
    List<Incident> findByPriority(IncidentPriority priority);
    List<Incident> findByTitleContainingIgnoreCase(String title);
    List<Incident> findByDescriptionContainingIgnoreCase(String description);
}