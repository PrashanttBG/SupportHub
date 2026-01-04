package com.support.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyMessageRequest {
    
    @NotNull(message = "Agent ID is required")
    private UUID agentId;
    
    @NotBlank(message = "Message content is required")
    private String content;
    
    private UUID cannedMessageId;
}

