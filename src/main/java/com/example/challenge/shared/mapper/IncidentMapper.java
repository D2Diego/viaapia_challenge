package com.example.challenge.shared.mapper;

import com.example.challenge.dto.request.IncidentCreateDto;
import com.example.challenge.dto.response.IncidentResponseDto;
import com.example.challenge.entity.Incident;
import org.springframework.stereotype.Component;


@Component
public class IncidentMapper {
    

    public IncidentResponseDto toResponseDto(Incident incident) {
        if (incident == null) {
            return null;
        }
        
        return new IncidentResponseDto(
            incident.getId(),
            incident.getTitle(),
            incident.getDescription(),
            incident.getPriority(),
            incident.getStatus(),
            incident.getResponsibleEmail(),
            incident.getTags(),
            incident.getCreatedAt(),
            incident.getUpdatedAt()
        );
    }
    

    public Incident toEntity(IncidentCreateDto createDto) {
        if (createDto == null) {
            return null;
        }
        
        Incident incident = new Incident();
        incident.setTitle(createDto.getTitle());
        incident.setDescription(createDto.getDescription());
        incident.setPriority(createDto.getPriority());
        incident.setStatus(createDto.getStatus());
        incident.setResponsibleEmail(createDto.getResponsibleEmail());
        incident.setTags(createDto.getTags());
        
        return incident;
    }
    

    public void updateEntityFromDto(Incident incident, IncidentCreateDto updateDto) {
        if (incident == null || updateDto == null) {
            return;
        }
        
        if (updateDto.getTitle() != null) {
            incident.setTitle(updateDto.getTitle());
        }
        
        if (updateDto.getDescription() != null) {
            incident.setDescription(updateDto.getDescription());
        }
        
        if (updateDto.getPriority() != null) {
            incident.setPriority(updateDto.getPriority());
        }
        
        if (updateDto.getStatus() != null) {
            incident.setStatus(updateDto.getStatus());
        }
        
        if (updateDto.getResponsibleEmail() != null) {
            incident.setResponsibleEmail(updateDto.getResponsibleEmail());
        }
        
        if (updateDto.getTags() != null) {
            incident.setTags(updateDto.getTags());
        }
    }
} 