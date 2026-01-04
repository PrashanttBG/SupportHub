package com.support.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMessageRequest {
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    private String customerEmail;
    private String customerPhone;
    
    @NotBlank(message = "Message content is required")
    private String content;
    
    private String subject;
}

