package com.example.challenge.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.challenge.entity.Incident;
import com.example.challenge.entity.IncidentPriority;
import com.example.challenge.entity.Status;
import com.example.challenge.repository.IncidentRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    @Autowired
    private IncidentRepository incidentRepository;

    // DTO para atualização de status
    public static class StatusUpdateRequest {
        @NotNull
        private Status status;
        
        public Status getStatus() {
            return status;
        }
        
        public void setStatus(Status status) {
            this.status = status;
        }
    }



    /**
     * GET /incidents - Lista simples com filtros opcionais
     */
    @GetMapping
    public ResponseEntity<List<Incident>> getAllIncidents(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) IncidentPriority priority,
            @RequestParam(required = false) String q) {
        
        try {
            List<Incident> incidents;
            
            if (status != null) {
                incidents = incidentRepository.findByStatus(status);
            } else if (priority != null) {
                incidents = incidentRepository.findByPriority(priority);
            } else if (q != null && !q.trim().isEmpty()) {
                incidents = incidentRepository.findByTitleContainingIgnoreCase(q);
            } else {
                incidents = incidentRepository.findAll();
            }
            
            return ResponseEntity.ok(incidents);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /incidents/{id} - Buscar incidente por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Incident> getIncidentById(@PathVariable UUID id) {
        try {
            Optional<Incident> incident = incidentRepository.findById(id);
            
            if (incident.isPresent()) {
                return ResponseEntity.ok(incident.get());
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /incidents - Criar novo incidente
     */
    @PostMapping
    public ResponseEntity<Incident> createIncident(@Valid @RequestBody Incident incident) {
        try {
            incident.setId(null);
            
            Incident savedIncident = incidentRepository.save(incident);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedIncident);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * PUT /incidents/{id} - Atualizar incidente existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Incident> updateIncident(@PathVariable UUID id, @Valid @RequestBody Incident incident) {
        try {
            Optional<Incident> existingIncident = incidentRepository.findById(id);
            
            if (existingIncident.isPresent()) {
                incident.setId(id);
                
                incident.setCreatedAt(existingIncident.get().getCreatedAt());
                
                Incident updatedIncident = incidentRepository.save(incident);
                return ResponseEntity.ok(updatedIncident);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * PATCH /incidents/{id}/status - Atualizar apenas o status do incidente
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Incident> updateIncidentStatus(@PathVariable UUID id, @Valid @RequestBody StatusUpdateRequest request) {
        try {
            Optional<Incident> existingIncident = incidentRepository.findById(id);
            
            if (existingIncident.isPresent()) {
                Incident incident = existingIncident.get();
                incident.setStatus(request.getStatus());
                
                Incident updatedIncident = incidentRepository.save(incident);
                return ResponseEntity.ok(updatedIncident);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * DELETE /incidents/{id} - Deletar incidente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable UUID id) {
        try {
            Optional<Incident> incident = incidentRepository.findById(id);
            
            if (incident.isPresent()) {
                incidentRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
