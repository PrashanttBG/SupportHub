package com.support.controller;

import com.support.dto.ConversationDTO;
import com.support.model.Conversation.ConversationStatus;
import com.support.model.Conversation.UrgencyLevel;
import com.support.service.ConversationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for conversation operations
 */
@RestController
@RequestMapping("/api/conversations")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    /**
     * Get all conversations with optional filters
     */
    @GetMapping
    public ResponseEntity<Page<ConversationDTO>> getConversations(
            @RequestParam(required = false) ConversationStatus status,
            @RequestParam(required = false) UrgencyLevel urgencyLevel,
            @RequestParam(required = false) UUID agentId,
            @PageableDefault(size = 20, sort = "urgencyScore", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(
                conversationService.getConversationsWithFilters(status, urgencyLevel, agentId, pageable)
        );
    }

    /**
     * Get open conversations sorted by urgency
     */
    @GetMapping("/open")
    public ResponseEntity<List<ConversationDTO>> getOpenConversations() {
        return ResponseEntity.ok(conversationService.getOpenConversations());
    }

    /**
     * Get unassigned conversations
     */
    @GetMapping("/unassigned")
    public ResponseEntity<List<ConversationDTO>> getUnassignedConversations() {
        return ResponseEntity.ok(conversationService.getUnassignedConversations());
    }

    /**
     * Get conversations for a specific agent
     */
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<ConversationDTO>> getAgentConversations(@PathVariable UUID agentId) {
        return ResponseEntity.ok(conversationService.getConversationsByAgent(agentId));
    }

    /**
     * Get conversations for a specific customer
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ConversationDTO>> getCustomerConversations(@PathVariable UUID customerId) {
        return ResponseEntity.ok(conversationService.getConversationsByCustomer(customerId));
    }

    /**
     * Get a single conversation by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConversationDTO> getConversation(@PathVariable UUID id) {
        return conversationService.getConversationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get conversation with all messages
     */
    @GetMapping("/{id}/full")
    public ResponseEntity<ConversationDTO> getConversationWithMessages(@PathVariable UUID id) {
        return conversationService.getConversationWithMessages(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Assign agent to conversation
     */
    @PutMapping("/{id}/assign")
    public ResponseEntity<ConversationDTO> assignAgent(
            @PathVariable UUID id,
            @RequestParam UUID agentId) {
        return ResponseEntity.ok(conversationService.assignAgent(id, agentId));
    }

    /**
     * Update conversation status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ConversationDTO> updateStatus(
            @PathVariable UUID id,
            @RequestParam ConversationStatus status) {
        return ResponseEntity.ok(conversationService.updateStatus(id, status));
    }

    /**
     * Mark all messages in conversation as read
     */
    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {
        conversationService.markMessagesAsRead(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Get conversation statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeConversations", conversationService.countActiveConversations());
        stats.put("urgentConversations", conversationService.countUrgentConversations());
        return ResponseEntity.ok(stats);
    }
}
