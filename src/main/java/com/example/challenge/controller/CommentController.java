package com.example.challenge.controller;

import com.example.challenge.dto.request.CommentCreateDto;
import com.example.challenge.dto.response.CommentResponseDto;
import com.example.challenge.service.CommentService;
import com.example.challenge.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/incidents")
public class CommentController {
    
    private final CommentService commentService;
    
    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
    

    @PostMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(
            @PathVariable UUID id, 
            @Valid @RequestBody CommentCreateDto createDto) {
        
        CommentResponseDto comment = commentService.createComment(id, createDto);
        ApiResponse<CommentResponseDto> response = ApiResponse.success("Comment created successfully", comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponseDto>>> getCommentsByIncident(@PathVariable UUID id) {
        List<CommentResponseDto> comments = commentService.findCommentsByIncident(id);
        ApiResponse<List<CommentResponseDto>> response = ApiResponse.success(comments);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/comments/{id}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> getCommentById(@PathVariable UUID id) {
        CommentResponseDto comment = commentService.findById(id);
        ApiResponse<CommentResponseDto> response = ApiResponse.success(comment);
        return ResponseEntity.ok(response);
    }
    

    @PutMapping("/comments/{id}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> updateComment(
            @PathVariable UUID id, 
            @Valid @RequestBody CommentCreateDto updateDto) {
        
        CommentResponseDto comment = commentService.updateComment(id, updateDto);
        ApiResponse<CommentResponseDto> response = ApiResponse.success("Comment updated successfully", comment);
        return ResponseEntity.ok(response);
    }
    

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable UUID id) {
        commentService.deleteComment(id);
        ApiResponse<Void> response = ApiResponse.success("Comment deleted successfully", null);
        return ResponseEntity.ok(response);
    }
    

    @GetMapping("/{id}/comments/count")
    public ResponseEntity<ApiResponse<Long>> countCommentsByIncident(@PathVariable UUID id) {
        long count = commentService.countCommentsByIncident(id);
        ApiResponse<Long> response = ApiResponse.success(count);
        return ResponseEntity.ok(response);
    }
}
