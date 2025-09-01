package com.example.challenge.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
     * GET /incidents - Lista paginada com filtros e ordenação
     * Parâmetros: status, priority, q (busca em título/descrição), page, size, sort=campo,asc|desc
     */
    @GetMapping
    public ResponseEntity<Page<Incident>> getAllIncidents(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) IncidentPriority priority,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        
        try {
            // Processar parâmetros de ordenação
            Pageable pageable = createPageable(page, size, sort);
            
            // Usar lógica de busca condicional baseada nos parâmetros
            Page<Incident> incidents = searchIncidents(status, priority, q, pageable);
            
            return ResponseEntity.ok(incidents);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Método auxiliar para criar Pageable com ordenação customizada
     */
    private Pageable createPageable(int page, int size, String sort) {
        try {
            String[] sortParams = sort.split(",");
            String field = sortParams[0];
            String direction = sortParams.length > 1 ? sortParams[1] : "asc";
            
            Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
            
            return PageRequest.of(page, size, Sort.by(sortDirection, field));
            
        } catch (Exception e) {
            // Em caso de erro na ordenação, usar padrão
            return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        }
    }

    /**
     * Método auxiliar para busca condicional baseada nos filtros
     */
    private Page<Incident> searchIncidents(Status status, IncidentPriority priority, String q, Pageable pageable) {
        // Se não há filtros, retorna todos
        if (status == null && priority == null && (q == null || q.trim().isEmpty())) {
            return incidentRepository.findAll(pageable);
        }
        
        // Se há termo de busca, fazemos busca em título e descrição
        boolean hasSearchTerm = q != null && !q.trim().isEmpty();
        
        // Combinações de filtros
        if (status != null && priority != null && hasSearchTerm) {
            // Buscar em título e descrição separadamente e combinar resultados
            Page<Incident> titleResults = incidentRepository.findByStatusAndPriorityAndTitleContainingIgnoreCase(
                status, priority, q, pageable);
            
            // Se título trouxe resultados suficientes, retorna
            if (titleResults.hasContent()) {
                return titleResults;
            }
            
            // Senão, busca na descrição
            return incidentRepository.findByStatusAndPriorityAndDescriptionContainingIgnoreCase(
                status, priority, q, pageable);
                
        } else if (status != null && hasSearchTerm) {
            // Status + busca de texto
            Page<Incident> titleResults = incidentRepository.findByStatusAndTitleContainingIgnoreCase(status, q, pageable);
            if (titleResults.hasContent()) {
                return titleResults;
            }
            return incidentRepository.findByStatusAndDescriptionContainingIgnoreCase(status, q, pageable);
            
        } else if (priority != null && hasSearchTerm) {
            // Priority + busca de texto
            Page<Incident> titleResults = incidentRepository.findByPriorityAndTitleContainingIgnoreCase(priority, q, pageable);
            if (titleResults.hasContent()) {
                return titleResults;
            }
            return incidentRepository.findByPriorityAndDescriptionContainingIgnoreCase(priority, q, pageable);
            
        } else if (hasSearchTerm) {
            // Apenas busca de texto
            return incidentRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(q, q, pageable);
            
        } else if (status != null) {
            // Apenas status
            return incidentRepository.findByStatus(status, pageable);
            
        } else if (priority != null) {
            // Apenas priority
            return incidentRepository.findByPriority(priority, pageable);
        }
        
        // Fallback - todos os incidents
        return incidentRepository.findAll(pageable);
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
