package com.support.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    private String phone;

    @Column(name = "external_profile_url")
    private String externalProfileUrl;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "account_status")
    private String accountStatus;

    @Column(name = "loan_status")
    private String loanStatus;

    @Column(name = "total_conversations")
    @Builder.Default
    private Integer totalConversations = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Conversation> conversations = new ArrayList<>();
}

