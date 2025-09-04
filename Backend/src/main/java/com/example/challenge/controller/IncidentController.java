package com.example.challenge.controller;

import com.example.challenge.dto.request.IncidentCreateDto;
import com.example.challenge.dto.request.StatusUpdateDto;
import com.example.challenge.dto.response.IncidentResponseDto;
import com.example.challenge.entity.IncidentPriority;
import com.example.challenge.entity.Status;
import com.example.challenge.service.IncidentService;
import com.example.challenge.shared.dto.ApiResponse;
import com.example.challenge.shared.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    @Autowired
    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }


    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<IncidentResponseDto>>> getAllIncidents(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) IncidentPriority priority,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        
        Pageable pageable = createPageable(page, size, sort);
        PageResponse<IncidentResponseDto> incidents = incidentService.findAll(status, priority, q, pageable);
        
        ApiResponse<PageResponse<IncidentResponseDto>> response = ApiResponse.success(incidents);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IncidentResponseDto>> getIncidentById(@PathVariable UUID id) {
        IncidentResponseDto incident = incidentService.findById(id);
        ApiResponse<IncidentResponseDto> response = ApiResponse.success(incident);
        return ResponseEntity.ok(response);
    }


    @PostMapping
    public ResponseEntity<ApiResponse<IncidentResponseDto>> createIncident(@Valid @RequestBody IncidentCreateDto createDto) {
        IncidentResponseDto incident = incidentService.createIncident(createDto);
        ApiResponse<IncidentResponseDto> response = ApiResponse.success("Incident created successfully", incident);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IncidentResponseDto>> updateIncident(
            @PathVariable UUID id, 
            @Valid @RequestBody IncidentCreateDto updateDto) {
        
        IncidentResponseDto incident = incidentService.updateIncident(id, updateDto);
        ApiResponse<IncidentResponseDto> response = ApiResponse.success("Incident updated successfully", incident);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<IncidentResponseDto>> updateIncidentStatus(
            @PathVariable UUID id, 
            @Valid @RequestBody StatusUpdateDto statusDto) {
        
        IncidentResponseDto incident = incidentService.updateStatus(id, statusDto);
        ApiResponse<IncidentResponseDto> response = ApiResponse.success("Incident status updated successfully", incident);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteIncident(@PathVariable UUID id) {
        incidentService.deleteIncident(id);
        ApiResponse<Void> response = ApiResponse.success("Incident deleted successfully", null);
        return ResponseEntity.ok(response);
    }


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
            return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        }
    }
}
