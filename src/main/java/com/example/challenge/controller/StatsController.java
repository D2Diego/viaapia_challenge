package com.example.challenge.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.challenge.entity.Incident;
import com.example.challenge.entity.IncidentPriority;
import com.example.challenge.entity.Status;
import com.example.challenge.repository.IncidentRepository;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private IncidentRepository incidentRepository;

    public static class IncidentStats {
        private Map<String, Long> byStatus;
        private Map<String, Long> byPriority;
        private Long total;
        
        public IncidentStats() {
            this.byStatus = new HashMap<>();
            this.byPriority = new HashMap<>();
        }
        
        public Map<String, Long> getByStatus() {
            return byStatus;
        }
        
        public void setByStatus(Map<String, Long> byStatus) {
            this.byStatus = byStatus;
        }
        
        public Map<String, Long> getByPriority() {
            return byPriority;
        }
        
        public void setByPriority(Map<String, Long> byPriority) {
            this.byPriority = byPriority;
        }
        
        public Long getTotal() {
            return total;
        }
        
        public void setTotal(Long total) {
            this.total = total;
        }
    }

    @GetMapping
    public ResponseEntity<IncidentStats> getStats() {
        return getIncidentStats();
    }
    
    @GetMapping("/incidents")
    public ResponseEntity<IncidentStats> getIncidentStats() {
        try {
            List<Incident> allIncidents = incidentRepository.findAll();
            
            IncidentStats stats = new IncidentStats();
            stats.setTotal((long) allIncidents.size());
            
            Map<String, Long> statusCounts = new HashMap<>();
            for (Status status : Status.values()) {
                long count = allIncidents.stream()
                    .filter(incident -> incident.getStatus() == status)
                    .count();
                statusCounts.put(status.name(), count);
            }
            stats.setByStatus(statusCounts);
            
            Map<String, Long> priorityCounts = new HashMap<>();
            for (IncidentPriority priority : IncidentPriority.values()) {
                long count = allIncidents.stream()
                    .filter(incident -> incident.getPriority() == priority)
                    .count();
                priorityCounts.put(priority.name(), count);
            }
            stats.setByPriority(priorityCounts);
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 