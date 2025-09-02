package com.example.challenge.shared.util;

import com.example.challenge.entity.Incident;
import java.time.LocalDateTime;


public class AuditUtils {
    

    public static void touchUpdate(Incident incident) {
        if (incident != null) {
            incident.setUpdatedAt(LocalDateTime.now());
        }
    }
    

    public static void touchCreate(Incident incident) {
        if (incident != null) {
            LocalDateTime now = LocalDateTime.now();
            incident.setCreatedAt(now);
            incident.setUpdatedAt(now);
        }
    }
    

    public static void touchUpdatePreservingCreated(Incident incident, LocalDateTime originalCreatedAt) {
        if (incident != null) {
            incident.setCreatedAt(originalCreatedAt);
            incident.setUpdatedAt(LocalDateTime.now());
        }
    }
    

    public static boolean canTouch(Incident incident) {
        return incident != null && incident.getId() != null;
    }
    

    public static long getMinutesSinceCreation(Incident incident) {
        if (incident == null || incident.getCreatedAt() == null) {
            return 0;
        }
        
        return java.time.Duration.between(incident.getCreatedAt(), LocalDateTime.now()).toMinutes();
    }
    

    public static long getMinutesSinceUpdate(Incident incident) {
        if (incident == null || incident.getUpdatedAt() == null) {
            return 0;
        }
        
        return java.time.Duration.between(incident.getUpdatedAt(), LocalDateTime.now()).toMinutes();
    }
} 