package com.support.dto;

import com.support.model.Agent;
import com.support.model.Agent.AgentStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentDTO {
    private UUID id;
    private String name;
    private String email;
    private String avatarUrl;
    private AgentStatus status;
    private Integer activeConversations;
    private Integer totalResolved;
    private LocalDateTime lastActive;
    private LocalDateTime createdAt;

    public static AgentDTO fromEntity(Agent agent) {
        return AgentDTO.builder()
                .id(agent.getId())
                .name(agent.getName())
                .email(agent.getEmail())
                .avatarUrl(agent.getAvatarUrl())
                .status(agent.getStatus())
                .activeConversations(agent.getActiveConversations())
                .totalResolved(agent.getTotalResolved())
                .lastActive(agent.getLastActive())
                .createdAt(agent.getCreatedAt())
                .build();
    }
}

