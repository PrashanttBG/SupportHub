package com.support.dto;

import com.support.model.Conversation;
import com.support.model.Conversation.ConversationStatus;
import com.support.model.Conversation.UrgencyLevel;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationDTO {
    private UUID id;
    private CustomerDTO customer;
    private AgentDTO assignedAgent;
    private String subject;
    private ConversationStatus status;
    private Integer urgencyScore;
    private String urgencyReason;
    private UrgencyLevel urgencyLevel;
    private Integer unreadCount;
    private LocalDateTime lastMessageAt;
    private String lastMessagePreview;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MessageDTO> messages;

    public static ConversationDTO fromEntity(Conversation conversation) {
        return ConversationDTO.builder()
                .id(conversation.getId())
                .customer(conversation.getCustomer() != null ? 
                        CustomerDTO.fromEntity(conversation.getCustomer()) : null)
                .assignedAgent(conversation.getAssignedAgent() != null ? 
                        AgentDTO.fromEntity(conversation.getAssignedAgent()) : null)
                .subject(conversation.getSubject())
                .status(conversation.getStatus())
                .urgencyScore(conversation.getUrgencyScore())
                .urgencyReason(conversation.getUrgencyReason())
                .urgencyLevel(conversation.getUrgencyLevel())
                .unreadCount(conversation.getUnreadCount())
                .lastMessageAt(conversation.getLastMessageAt())
                .lastMessagePreview(conversation.getLastMessagePreview())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }

    public static ConversationDTO fromEntityWithMessages(Conversation conversation) {
        ConversationDTO dto = fromEntity(conversation);
        if (conversation.getMessages() != null) {
            dto.setMessages(conversation.getMessages().stream()
                    .map(MessageDTO::fromEntity)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}

