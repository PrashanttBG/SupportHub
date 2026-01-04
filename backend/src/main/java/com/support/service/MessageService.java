package com.support.service;

import com.support.dto.MessageDTO;
import com.support.dto.request.CreateMessageRequest;
import com.support.dto.request.ReplyMessageRequest;
import com.support.model.*;
import com.support.model.Conversation.ConversationStatus;
import com.support.model.Message.SenderType;
import com.support.repository.CannedMessageRepository;
import com.support.repository.ConversationRepository;
import com.support.repository.MessageRepository;
import com.support.service.UrgencyDetectionService.UrgencyResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for handling message operations
 */
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final CannedMessageRepository cannedMessageRepository;
    private final CustomerService customerService;
    private final AgentService agentService;
    private final UrgencyDetectionService urgencyDetectionService;

    public MessageService(MessageRepository messageRepository,
                         ConversationRepository conversationRepository,
                         CannedMessageRepository cannedMessageRepository,
                         CustomerService customerService,
                         AgentService agentService,
                         UrgencyDetectionService urgencyDetectionService) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.cannedMessageRepository = cannedMessageRepository;
        this.customerService = customerService;
        this.agentService = agentService;
        this.urgencyDetectionService = urgencyDetectionService;
    }

    /**
     * Get all messages for a conversation
     */
    public List<MessageDTO> getMessagesByConversation(UUID conversationId) {
        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
        List<MessageDTO> result = new ArrayList<>();
        for (Message msg : messages) {
            result.add(MessageDTO.fromEntity(msg));
        }
        return result;
    }

    /**
     * Get a single message by ID
     */
    public Optional<MessageDTO> getMessageById(UUID id) {
        Optional<Message> message = messageRepository.findById(id);
        if (message.isPresent()) {
            return Optional.of(MessageDTO.fromEntity(message.get()));
        }
        return Optional.empty();
    }

    /**
     * Search messages by content
     */
    public Page<MessageDTO> searchMessages(String query, Pageable pageable) {
        return messageRepository.searchMessages(query, pageable)
                .map(MessageDTO::fromEntity);
    }

    /**
     * Create a new message from customer (creates new conversation)
     */
    @Transactional
    public MessageDTO createCustomerMessage(CreateMessageRequest request) {
        // Find or create customer
        Customer customer = customerService.findOrCreateCustomer(
                request.getCustomerName(),
                request.getCustomerEmail(),
                request.getCustomerPhone()
        );

        // Analyze urgency
        UrgencyResult urgency = urgencyDetectionService.analyzeUrgency(request.getContent());

        // Create subject from content if not provided
        String subject = request.getSubject();
        if (subject == null || subject.isEmpty()) {
            subject = truncate(request.getContent(), 50);
        }

        // Create new conversation
        Conversation conversation = new Conversation();
        conversation.setCustomer(customer);
        conversation.setSubject(subject);
        conversation.setStatus(ConversationStatus.OPEN);
        conversation.setUrgencyScore(urgency.getScore());
        conversation.setUrgencyLevel(urgency.getLevel());
        conversation.setUrgencyReason(urgency.getReason());
        conversation.setLastMessageAt(LocalDateTime.now());
        conversation.setLastMessagePreview(truncate(request.getContent(), 100));
        conversation.setUnreadCount(1);
        conversation = conversationRepository.save(conversation);

        // Update customer conversation count
        customerService.incrementConversationCount(customer.getId());

        // Create message
        Message message = new Message();
        message.setConversation(conversation);
        message.setSenderId(customer.getId());
        message.setSenderType(SenderType.CUSTOMER);
        message.setSenderName(customer.getName());
        message.setContent(request.getContent());
        message.setIsRead(false);
        message = messageRepository.save(message);

        return MessageDTO.fromEntity(message);
    }

    /**
     * Add message to existing conversation
     */
    @Transactional
    public MessageDTO addMessageToConversation(UUID conversationId, CreateMessageRequest request) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        Customer customer = conversation.getCustomer();

        // Update urgency if new message is more urgent
        UrgencyResult urgency = urgencyDetectionService.analyzeUrgency(request.getContent());
        if (urgency.getScore() > conversation.getUrgencyScore()) {
            conversation.setUrgencyScore(urgency.getScore());
            conversation.setUrgencyLevel(urgency.getLevel());
            conversation.setUrgencyReason(urgency.getReason());
        }

        // Update conversation
        conversation.setLastMessageAt(LocalDateTime.now());
        conversation.setLastMessagePreview(truncate(request.getContent(), 100));
        conversation.setUnreadCount(conversation.getUnreadCount() + 1);
        
        // Reopen if was closed
        if (conversation.getStatus() == ConversationStatus.RESOLVED || 
            conversation.getStatus() == ConversationStatus.CLOSED) {
            conversation.setStatus(ConversationStatus.OPEN);
        }
        conversationRepository.save(conversation);

        // Create message
        Message message = new Message();
        message.setConversation(conversation);
        message.setSenderId(customer.getId());
        message.setSenderType(SenderType.CUSTOMER);
        message.setSenderName(customer.getName());
        message.setContent(request.getContent());
        message.setIsRead(false);
        message = messageRepository.save(message);

        return MessageDTO.fromEntity(message);
    }

    /**
     * Create agent reply to conversation
     */
    @Transactional
    public MessageDTO createAgentReply(UUID conversationId, ReplyMessageRequest request) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        Agent agent = agentService.findById(request.getAgentId())
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        String content = request.getContent();
        boolean isCannedResponse = false;

        // Check if using canned message
        if (request.getCannedMessageId() != null) {
            CannedMessage canned = cannedMessageRepository.findById(request.getCannedMessageId())
                    .orElseThrow(() -> new RuntimeException("Canned message not found"));
            content = canned.getContent();
            isCannedResponse = true;
            
            // Update usage count
            canned.setUsageCount(canned.getUsageCount() + 1);
            cannedMessageRepository.save(canned);
        }

        // Assign agent if not already assigned
        if (conversation.getAssignedAgent() == null) {
            conversation.setAssignedAgent(agent);
            agentService.incrementActiveConversations(agent.getId());
        }

        // Update conversation status
        conversation.setStatus(ConversationStatus.IN_PROGRESS);
        conversation.setLastMessageAt(LocalDateTime.now());
        conversation.setLastMessagePreview(truncate(content, 100));
        conversation.setUnreadCount(0);
        conversationRepository.save(conversation);

        // Mark customer messages as read
        messageRepository.markAllAsReadForConversation(conversationId);

        // Create agent message
        Message message = new Message();
        message.setConversation(conversation);
        message.setSenderId(agent.getId());
        message.setSenderType(SenderType.AGENT);
        message.setSenderName(agent.getName());
        message.setContent(content);
        message.setIsRead(true);
        message.setIsCannedResponse(isCannedResponse);
        message = messageRepository.save(message);

        return MessageDTO.fromEntity(message);
    }

    /**
     * Helper to truncate text
     */
    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}
