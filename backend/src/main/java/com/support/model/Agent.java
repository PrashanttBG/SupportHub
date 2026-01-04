package com.support.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "agents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AgentStatus status = AgentStatus.OFFLINE;

    @Column(name = "active_conversations")
    @Builder.Default
    private Integer activeConversations = 0;

    @Column(name = "total_resolved")
    @Builder.Default
    private Integer totalResolved = 0;

    @Column(name = "last_active")
    private LocalDateTime lastActive;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "assignedAgent", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Conversation> assignedConversations = new ArrayList<>();

    public enum AgentStatus {
        ONLINE, AWAY, BUSY, OFFLINE
    }
}

