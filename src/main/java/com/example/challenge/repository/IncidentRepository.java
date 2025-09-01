package com.example.challenge.repository;

import java.util.UUID;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    // Métodos com paginação
    Page<Incident> findByStatus(Status status, Pageable pageable);
    Page<Incident> findByPriority(IncidentPriority priority, Pageable pageable);
    
    // Busca em título OU descrição com paginação - usando Spring Data JPA method naming
    Page<Incident> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String titleSearch, String descriptionSearch, Pageable pageable);
    
    // Métodos para busca combinada usando Spring Data JPA method naming
    Page<Incident> findByStatusAndPriorityAndTitleContainingIgnoreCase(
        Status status, IncidentPriority priority, String title, Pageable pageable);
    
    Page<Incident> findByStatusAndPriorityAndDescriptionContainingIgnoreCase(
        Status status, IncidentPriority priority, String description, Pageable pageable);
    
    Page<Incident> findByStatusAndTitleContainingIgnoreCase(
        Status status, String title, Pageable pageable);
        
    Page<Incident> findByStatusAndDescriptionContainingIgnoreCase(
        Status status, String description, Pageable pageable);
        
    Page<Incident> findByPriorityAndTitleContainingIgnoreCase(
        IncidentPriority priority, String title, Pageable pageable);
        
    Page<Incident> findByPriorityAndDescriptionContainingIgnoreCase(
        IncidentPriority priority, String description, Pageable pageable);
        
    // Busca só por texto (título ou descrição)
    Page<Incident> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Incident> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);
}