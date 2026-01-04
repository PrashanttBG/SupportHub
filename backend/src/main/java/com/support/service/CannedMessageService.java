package com.support.service;

import com.support.dto.CannedMessageDTO;
import com.support.model.CannedMessage;
import com.support.repository.CannedMessageRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing canned (template) messages
 */
@Service
public class CannedMessageService {

    private final CannedMessageRepository cannedMessageRepository;

    public CannedMessageService(CannedMessageRepository cannedMessageRepository) {
        this.cannedMessageRepository = cannedMessageRepository;
    }

    /**
     * Get all active canned messages
     */
    public List<CannedMessageDTO> getAllActiveCannedMessages() {
        List<CannedMessage> messages = cannedMessageRepository.findByIsActiveTrue();
        List<CannedMessageDTO> result = new ArrayList<>();
        for (CannedMessage msg : messages) {
            result.add(CannedMessageDTO.fromEntity(msg));
        }
        return result;
    }

    /**
     * Get canned messages by category
     */
    public List<CannedMessageDTO> getCannedMessagesByCategory(String category) {
        List<CannedMessage> messages = cannedMessageRepository.findByCategoryAndIsActiveTrue(category);
        List<CannedMessageDTO> result = new ArrayList<>();
        for (CannedMessage msg : messages) {
            result.add(CannedMessageDTO.fromEntity(msg));
        }
        return result;
    }

    /**
     * Get canned message by ID
     */
    public Optional<CannedMessageDTO> getCannedMessageById(UUID id) {
        Optional<CannedMessage> msg = cannedMessageRepository.findById(id);
        if (msg.isPresent()) {
            return Optional.of(CannedMessageDTO.fromEntity(msg.get()));
        }
        return Optional.empty();
    }

    /**
     * Get all categories
     */
    public List<String> getAllCategories() {
        return cannedMessageRepository.findAllCategories();
    }

    /**
     * Search canned messages
     */
    public List<CannedMessageDTO> searchCannedMessages(String query) {
        List<CannedMessage> messages = cannedMessageRepository.searchCannedMessages(query);
        List<CannedMessageDTO> result = new ArrayList<>();
        for (CannedMessage msg : messages) {
            result.add(CannedMessageDTO.fromEntity(msg));
        }
        return result;
    }
}
