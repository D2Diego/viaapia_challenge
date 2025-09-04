package com.example.challenge.shared.util;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utilitários para normalização de tags
 */
public class TagUtils {
    

    public static List<String> normalizeTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        
        List<String> normalizedTags = tags.stream()
            .filter(tag -> tag != null && !tag.trim().isEmpty()) // Remove nulos e vazios
            .map(String::trim)                                    // Aplica trim
            .map(String::toLowerCase)                            // Converte para minúsculas
            .distinct()                                          // Remove duplicatas
            .sorted()                                            // Ordena alfabeticamente
            .collect(Collectors.toList());
        
        return normalizedTags.isEmpty() ? null : normalizedTags;
    }
    

    public static boolean isValidTag(String tag) {
        return tag != null && !tag.trim().isEmpty();
    }
    

    public static long countValidTags(List<String> tags) {
        if (tags == null) {
            return 0;
        }
        
        return tags.stream()
            .filter(TagUtils::isValidTag)
            .count();
    }
    

    public static String tagsToString(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "[]";
        }
        
        return "[" + String.join(", ", tags) + "]";
    }
} 