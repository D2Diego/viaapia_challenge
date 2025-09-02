package com.example.challenge.shared.util;

import com.example.challenge.entity.Incident;
import com.example.challenge.entity.IncidentPriority;
import com.example.challenge.entity.Status;
import com.example.challenge.repository.IncidentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public class FilterUtils {
    

    public static class IncidentFilters {
        private final Status status;
        private final IncidentPriority priority;
        private final String searchTerm;
        
        public IncidentFilters(Status status, IncidentPriority priority, String searchTerm) {
            this.status = status;
            this.priority = priority;
            this.searchTerm = searchTerm;
        }
        
        public Status getStatus() { return status; }
        public IncidentPriority getPriority() { return priority; }
        public String getSearchTerm() { return searchTerm; }
        
        public boolean hasStatus() { return status != null; }
        public boolean hasPriority() { return priority != null; }
        public boolean hasSearchTerm() { return searchTerm != null && !searchTerm.trim().isEmpty(); }
        public boolean hasAnyFilter() { return hasStatus() || hasPriority() || hasSearchTerm(); }
    }
    

    @FunctionalInterface
    public interface RepositoryOperation<T> {
        Page<T> execute();
    }
    
    /**
     * @param repository Repository para executar queries
     * @param filters Filtros encapsulados
     * @param pageable Configuração de paginação
     * @return Page de incidents filtrados
     */
    public static Page<Incident> buildIncidentFilter(IncidentRepository repository, 
                                                   IncidentFilters filters, 
                                                   Pageable pageable) {
        
        if (!filters.hasAnyFilter()) {
            return repository.findAll(pageable);
        }
        
        if (filters.hasStatus() && filters.hasPriority() && filters.hasSearchTerm()) {
            return executeWithFallback(
                () -> repository.findByStatusAndPriorityAndTitleContainingIgnoreCase(
                    filters.getStatus(), filters.getPriority(), filters.getSearchTerm(), pageable),
                () -> repository.findByStatusAndPriorityAndDescriptionContainingIgnoreCase(
                    filters.getStatus(), filters.getPriority(), filters.getSearchTerm(), pageable)
            );
        }
        
        if (filters.hasStatus() && filters.hasSearchTerm()) {
            return executeWithFallback(
                () -> repository.findByStatusAndTitleContainingIgnoreCase(
                    filters.getStatus(), filters.getSearchTerm(), pageable),
                () -> repository.findByStatusAndDescriptionContainingIgnoreCase(
                    filters.getStatus(), filters.getSearchTerm(), pageable)
            );
        }
        
        if (filters.hasPriority() && filters.hasSearchTerm()) {
            return executeWithFallback(
                () -> repository.findByPriorityAndTitleContainingIgnoreCase(
                    filters.getPriority(), filters.getSearchTerm(), pageable),
                () -> repository.findByPriorityAndDescriptionContainingIgnoreCase(
                    filters.getPriority(), filters.getSearchTerm(), pageable)
            );
        }
        
        if (filters.hasSearchTerm()) {
            return repository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                filters.getSearchTerm(), filters.getSearchTerm(), pageable);
        }
        
        if (filters.hasStatus()) {
            return repository.findByStatus(filters.getStatus(), pageable);
        }
        
        if (filters.hasPriority()) {
            return repository.findByPriority(filters.getPriority(), pageable);
        }
        
        return repository.findAll(pageable);
    }
    

    
    private static Page<Incident> executeWithFallback(RepositoryOperation<Incident> primary, 
                                                     RepositoryOperation<Incident> fallback) {
        Page<Incident> primaryResults = primary.execute();
        
        if (primaryResults.hasContent()) {
            return primaryResults;
        }
        
        return fallback.execute();
    }
    

    public static String normalizeSearchTerm(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return null;
        }
        
        return searchTerm.trim();
    }
    

    public static boolean areFiltersValid(IncidentFilters filters) {
        return filters != null && filters.hasAnyFilter();
    }
} 