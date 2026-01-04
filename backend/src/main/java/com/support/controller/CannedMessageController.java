package com.support.controller;

import com.support.dto.CannedMessageDTO;
import com.support.service.CannedMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for canned message operations
 */
@RestController
@RequestMapping("/api/canned-messages")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class CannedMessageController {

    private final CannedMessageService cannedMessageService;

    public CannedMessageController(CannedMessageService cannedMessageService) {
        this.cannedMessageService = cannedMessageService;
    }

    /**
     * Get all active canned messages
     */
    @GetMapping
    public ResponseEntity<List<CannedMessageDTO>> getAllCannedMessages() {
        return ResponseEntity.ok(cannedMessageService.getAllActiveCannedMessages());
    }

    /**
     * Get canned messages by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<CannedMessageDTO>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(cannedMessageService.getCannedMessagesByCategory(category));
    }

    /**
     * Get all categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(cannedMessageService.getAllCategories());
    }

    /**
     * Get canned message by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CannedMessageDTO> getCannedMessageById(@PathVariable UUID id) {
        return cannedMessageService.getCannedMessageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Search canned messages
     */
    @GetMapping("/search")
    public ResponseEntity<List<CannedMessageDTO>> searchCannedMessages(@RequestParam String query) {
        return ResponseEntity.ok(cannedMessageService.searchCannedMessages(query));
    }
}
