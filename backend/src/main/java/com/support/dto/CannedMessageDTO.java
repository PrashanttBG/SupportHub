package com.support.dto;

import com.support.model.CannedMessage;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CannedMessageDTO {
    private UUID id;
    private String title;
    private String category;
    private String content;
    private String shortcut;
    private Integer usageCount;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public static CannedMessageDTO fromEntity(CannedMessage cannedMessage) {
        return CannedMessageDTO.builder()
                .id(cannedMessage.getId())
                .title(cannedMessage.getTitle())
                .category(cannedMessage.getCategory())
                .content(cannedMessage.getContent())
                .shortcut(cannedMessage.getShortcut())
                .usageCount(cannedMessage.getUsageCount())
                .isActive(cannedMessage.getIsActive())
                .createdAt(cannedMessage.getCreatedAt())
                .build();
    }
}

