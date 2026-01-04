package com.support.dto;

import com.support.model.Customer;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDTO {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String externalProfileUrl;
    private String metadata;
    private String accountStatus;
    private String loanStatus;
    private Integer totalConversations;
    private LocalDateTime createdAt;

    public static CustomerDTO fromEntity(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .externalProfileUrl(customer.getExternalProfileUrl())
                .metadata(customer.getMetadata())
                .accountStatus(customer.getAccountStatus())
                .loanStatus(customer.getLoanStatus())
                .totalConversations(customer.getTotalConversations())
                .createdAt(customer.getCreatedAt())
                .build();
    }
}

