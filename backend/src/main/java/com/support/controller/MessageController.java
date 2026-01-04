package com.support.controller;

import com.support.dto.MessageDTO;
import com.support.dto.request.CreateMessageRequest;
import com.support.dto.request.ReplyMessageRequest;
import com.support.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for message operations
 */
@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Create new message (starts new conversation)
     */
    @PostMapping
    public ResponseEntity<MessageDTO> createMessage(@Valid @RequestBody CreateMessageRequest request) {
        MessageDTO message = messageService.createCustomerMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    /**
     * Add message to existing conversation
     */
    @PostMapping("/conversation/{conversationId}")
    public ResponseEntity<MessageDTO> addMessage(
            @PathVariable UUID conversationId,
            @Valid @RequestBody CreateMessageRequest request) {
        MessageDTO message = messageService.addMessageToConversation(conversationId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    /**
     * Agent replies to conversation
     */
    @PostMapping("/conversation/{conversationId}/reply")
    public ResponseEntity<MessageDTO> replyToConversation(
            @PathVariable UUID conversationId,
            @Valid @RequestBody ReplyMessageRequest request) {
        MessageDTO message = messageService.createAgentReply(conversationId, request);
        return ResponseEntity.ok(message);
    }

    /**
     * Get messages for a conversation
     */
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<MessageDTO>> getMessagesByConversation(@PathVariable UUID conversationId) {
        return ResponseEntity.ok(messageService.getMessagesByConversation(conversationId));
    }

    /**
     * Get single message by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<MessageDTO> getMessage(@PathVariable UUID id) {
        return messageService.getMessageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Search messages
     */
    @GetMapping("/search")
    public ResponseEntity<Page<MessageDTO>> searchMessages(
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(messageService.searchMessages(query, pageable));
    }
}
