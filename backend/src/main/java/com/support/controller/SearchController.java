package com.support.controller;

import com.support.dto.CustomerDTO;
import com.support.dto.MessageDTO;
import com.support.service.CustomerService;
import com.support.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for search operations
 */
@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class SearchController {

    private final MessageService messageService;
    private final CustomerService customerService;

    public SearchController(MessageService messageService, CustomerService customerService) {
        this.messageService = messageService;
        this.customerService = customerService;
    }

    /**
     * Global search across messages and customers
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> globalSearch(
            @RequestParam String query,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Map<String, Object> results = new HashMap<>();
        
        // Trim and validate query
        String trimmedQuery = query.trim();
        if (trimmedQuery.isEmpty()) {
            results.put("messages", List.of());
            results.put("messagesTotal", 0L);
            results.put("customers", List.of());
            results.put("customersTotal", 0L);
            return ResponseEntity.ok(results);
        }
        
        // Search messages
        Page<MessageDTO> messages = messageService.searchMessages(trimmedQuery, pageable);
        results.put("messages", messages.getContent());
        results.put("messagesTotal", messages.getTotalElements());
        
        // Search customers
        List<CustomerDTO> customers = customerService.searchCustomers(trimmedQuery);
        results.put("customers", customers);
        results.put("customersTotal", (long) customers.size());
        
        return ResponseEntity.ok(results);
    }

    /**
     * Search messages only
     */
    @GetMapping("/messages")
    public ResponseEntity<Page<MessageDTO>> searchMessages(
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable) {
        String trimmedQuery = query.trim();
        if (trimmedQuery.isEmpty()) {
            return ResponseEntity.ok(Page.empty());
        }
        return ResponseEntity.ok(messageService.searchMessages(trimmedQuery, pageable));
    }

    /**
     * Search customers only
     */
    @GetMapping("/customers")
    public ResponseEntity<List<CustomerDTO>> searchCustomers(@RequestParam String query) {
        String trimmedQuery = query.trim();
        if (trimmedQuery.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(customerService.searchCustomers(trimmedQuery));
    }
}
