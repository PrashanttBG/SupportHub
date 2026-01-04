package com.support.dto;

import com.support.model.Message;
import com.support.model.Message.SenderType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    private UUID id;
    private UUID conversationId;
    private UUID senderId;
    private SenderType senderType;
    private String senderName;
    private String content;
    private Boolean isRead;
    private Boolean isCannedResponse;
    private LocalDateTime createdAt;

    public static MessageDTO fromEntity(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .conversationId(message.getConversation() != null ? 
                        message.getConversation().getId() : null)
                .senderId(message.getSenderId())
                .senderType(message.getSenderType())
                .senderName(message.getSenderName())
                .content(message.getContent())
                .isRead(message.getIsRead())
                .isCannedResponse(message.getIsCannedResponse())
                .createdAt(message.getCreatedAt())
                .build();
    }
}

