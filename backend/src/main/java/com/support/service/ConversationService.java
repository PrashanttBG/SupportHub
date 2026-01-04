package com.support.service;

import com.support.dto.ConversationDTO;
import com.support.model.Agent;
import com.support.model.Conversation;
import com.support.model.Conversation.ConversationStatus;
import com.support.model.Conversation.UrgencyLevel;
import com.support.repository.ConversationRepository;
import com.support.repository.MessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for handling conversation operations
 */
@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AgentService agentService;

    public ConversationService(ConversationRepository conversationRepository,
                              MessageRepository messageRepository,
                              AgentService agentService) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.agentService = agentService;
    }

    /**
     * Get all conversations
     */
    public List<ConversationDTO> getAllConversations() {
        List<Conversation> conversations = conversationRepository.findAll();
        List<ConversationDTO> result = new ArrayList<>();
        for (Conversation conv : conversations) {
            result.add(ConversationDTO.fromEntity(conv));
        }
        return result;
    }

    /**
     * Get conversations with filters and pagination
     */
    public Page<ConversationDTO> getConversationsWithFilters(
            ConversationStatus status,
            UrgencyLevel urgencyLevel,
            UUID agentId,
            Pageable pageable) {
        return conversationRepository.findWithFilters(status, urgencyLevel, agentId, pageable)
                .map(ConversationDTO::fromEntity);
    }

    /**
     * Get single conversation by ID
     */
    public Optional<ConversationDTO> getConversationById(UUID id) {
        Optional<Conversation> conv = conversationRepository.findById(id);
        if (conv.isPresent()) {
            return Optional.of(ConversationDTO.fromEntity(conv.get()));
        }
        return Optional.empty();
    }

    /**
     * Get conversation with all messages
     */
    @Transactional(readOnly = true)
    public Optional<ConversationDTO> getConversationWithMessages(UUID id) {
        Optional<Conversation> conversationOpt = conversationRepository.findByIdWithMessages(id);
        if (conversationOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Conversation conversation = conversationOpt.get();
        // Ensure messages are loaded and sorted
        if (conversation.getMessages() != null) {
            conversation.getMessages().sort((a, b) -> 
                a.getCreatedAt().compareTo(b.getCreatedAt())
            );
        }
        
        return Optional.of(ConversationDTO.fromEntityWithMessages(conversation));
    }

    /**
     * Get all open conversations sorted by urgency
     */
    public List<ConversationDTO> getOpenConversations() {
        List<ConversationStatus> openStatuses = Arrays.asList(
                ConversationStatus.OPEN,
                ConversationStatus.IN_PROGRESS,
                ConversationStatus.WAITING
        );
        List<Conversation> conversations = conversationRepository.findOpenConversationsOrderByUrgency(openStatuses);
        
        List<ConversationDTO> result = new ArrayList<>();
        for (Conversation conv : conversations) {
            result.add(ConversationDTO.fromEntity(conv));
        }
        return result;
    }

    /**
     * Get unassigned conversations
     */
    public List<ConversationDTO> getUnassignedConversations() {
        List<Conversation> conversations = conversationRepository.findUnassignedConversations();
        List<ConversationDTO> result = new ArrayList<>();
        for (Conversation conv : conversations) {
            result.add(ConversationDTO.fromEntity(conv));
        }
        return result;
    }

    /**
     * Get conversations assigned to specific agent
     */
    public List<ConversationDTO> getConversationsByAgent(UUID agentId) {
        List<Conversation> conversations = conversationRepository.findActiveConversationsByAgent(agentId);
        List<ConversationDTO> result = new ArrayList<>();
        for (Conversation conv : conversations) {
            result.add(ConversationDTO.fromEntity(conv));
        }
        return result;
    }

    /**
     * Get conversations for a customer
     */
    public List<ConversationDTO> getConversationsByCustomer(UUID customerId) {
        List<Conversation> conversations = conversationRepository.findByCustomerId(customerId);
        List<ConversationDTO> result = new ArrayList<>();
        for (Conversation conv : conversations) {
            result.add(ConversationDTO.fromEntity(conv));
        }
        return result;
    }

    /**
     * Assign an agent to a conversation
     */
    @Transactional
    public ConversationDTO assignAgent(UUID conversationId, UUID agentId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        Agent agent = agentService.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        // If previous agent exists, update their count
        if (conversation.getAssignedAgent() != null) {
            agentService.decrementActiveConversations(conversation.getAssignedAgent().getId());
        }

        conversation.setAssignedAgent(agent);
        conversation.setStatus(ConversationStatus.IN_PROGRESS);
        agentService.incrementActiveConversations(agentId);

        Conversation saved = conversationRepository.save(conversation);
        return ConversationDTO.fromEntity(saved);
    }

    /**
     * Update conversation status
     */
    @Transactional
    public ConversationDTO updateStatus(UUID conversationId, ConversationStatus status) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        conversation.setStatus(status);

        // Handle resolved status - update agent stats
        if (status == ConversationStatus.RESOLVED && conversation.getAssignedAgent() != null) {
            agentService.decrementActiveConversations(conversation.getAssignedAgent().getId());
            agentService.incrementResolvedCount(conversation.getAssignedAgent().getId());
        }

        Conversation saved = conversationRepository.save(conversation);
        return ConversationDTO.fromEntity(saved);
    }

    /**
     * Mark all messages in conversation as read
     */
    @Transactional
    public void markMessagesAsRead(UUID conversationId) {
        messageRepository.markAllAsReadForConversation(conversationId);
        
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        conversation.setUnreadCount(0);
        conversationRepository.save(conversation);
    }

    /**
     * Count active conversations
     */
    public long countActiveConversations() {
        return conversationRepository.countActiveConversations();
    }

    /**
     * Count urgent conversations
     */
    public long countUrgentConversations() {
        return conversationRepository.countUrgentConversations();
    }
}
