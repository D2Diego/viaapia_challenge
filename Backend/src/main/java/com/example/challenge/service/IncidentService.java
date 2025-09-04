package com.example.challenge.service;

import com.example.challenge.dto.request.IncidentCreateDto;
import com.example.challenge.dto.request.StatusUpdateDto;
import com.example.challenge.dto.response.IncidentResponseDto;
import com.example.challenge.entity.Incident;
import com.example.challenge.entity.IncidentPriority;
import com.example.challenge.entity.Status;
import com.example.challenge.repository.IncidentRepository;
import com.example.challenge.shared.dto.PageResponse;
import com.example.challenge.shared.exception.NotFoundException;
import com.example.challenge.shared.mapper.IncidentMapper;
import com.example.challenge.shared.util.AuditUtils;
import com.example.challenge.shared.util.FilterUtils;
import com.example.challenge.shared.util.TagUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@Transactional
public class IncidentService {
    
    private final IncidentRepository incidentRepository;
    private final IncidentMapper incidentMapper;
    
    @Autowired
    public IncidentService(IncidentRepository incidentRepository,
                          IncidentMapper incidentMapper) {
        this.incidentRepository = incidentRepository;
        this.incidentMapper = incidentMapper;
    }
    

    public IncidentResponseDto createIncident(IncidentCreateDto createDto) {

        Incident incident = incidentMapper.toEntity(createDto);
        
        if (incident.getStatus() == null) {
            incident.setStatus(Status.OPEN);
        }
        
        incident.setTags(TagUtils.normalizeTags(incident.getTags()));
        
        AuditUtils.touchCreate(incident);
        
        Incident savedIncident = incidentRepository.save(incident);
        
        return incidentMapper.toResponseDto(savedIncident);
    }
    

    @Transactional(readOnly = true)
    public IncidentResponseDto findById(UUID id) {
        Incident incident = incidentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Incident", id));
            
        return incidentMapper.toResponseDto(incident);
    }
    

    @Transactional(readOnly = true)
    public PageResponse<IncidentResponseDto> findAll(Status status, IncidentPriority priority, 
                                                    String q, Pageable pageable) {
        
        FilterUtils.IncidentFilters filters = new FilterUtils.IncidentFilters(
            status, 
            priority, 
            FilterUtils.normalizeSearchTerm(q)
        );
        
        Page<Incident> incidentPage = FilterUtils.buildIncidentFilter(
            (IncidentRepository) incidentRepository, 
            filters, 
            pageable
        );
        
        Page<IncidentResponseDto> dtoPage = incidentPage.map(incidentMapper::toResponseDto);
        
        return PageResponse.of(dtoPage);
    }
    

    public IncidentResponseDto updateIncident(UUID id, IncidentCreateDto updateDto) {
        Incident existingIncident = incidentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Incident", id));
        
        var originalCreatedAt = existingIncident.getCreatedAt();
        
        incidentMapper.updateEntityFromDto(existingIncident, updateDto);
        
        existingIncident.setTags(TagUtils.normalizeTags(existingIncident.getTags()));
        
        AuditUtils.touchUpdatePreservingCreated(existingIncident, originalCreatedAt);
        
        Incident savedIncident = incidentRepository.save(existingIncident);
        return incidentMapper.toResponseDto(savedIncident);
    }
    

    public IncidentResponseDto updateStatus(UUID id, StatusUpdateDto statusDto) {
        Incident existingIncident = incidentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Incident", id));
        
        existingIncident.setStatus(statusDto.getStatus());
        
        AuditUtils.touchUpdate(existingIncident);
        
        Incident savedIncident = incidentRepository.save(existingIncident);
        return incidentMapper.toResponseDto(savedIncident);
    }
    

    public void deleteIncident(UUID id) {
        if (!incidentRepository.existsById(id)) {
            throw new NotFoundException("Incident", id);
        }
        
        incidentRepository.deleteById(id);
    }
} 