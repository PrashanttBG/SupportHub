package com.support.service;

import com.support.dto.AgentDTO;
import com.support.dto.request.CreateAgentRequest;
import com.support.model.Agent;
import com.support.model.Agent.AgentStatus;
import com.support.repository.AgentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing support agents
 */
@Service
public class AgentService {

    private final AgentRepository agentRepository;

    public AgentService(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    /**
     * Get all agents
     */
    public List<AgentDTO> getAllAgents() {
        List<Agent> agents = agentRepository.findAll();
        List<AgentDTO> result = new ArrayList<>();
        for (Agent agent : agents) {
            result.add(AgentDTO.fromEntity(agent));
        }
        return result;
    }

    /**
     * Get agent by ID
     */
    public Optional<AgentDTO> getAgentById(UUID id) {
        Optional<Agent> agent = agentRepository.findById(id);
        if (agent.isPresent()) {
            return Optional.of(AgentDTO.fromEntity(agent.get()));
        }
        return Optional.empty();
    }

    /**
     * Get online agents only
     */
    public List<AgentDTO> getOnlineAgents() {
        List<Agent> agents = agentRepository.findByStatus(AgentStatus.ONLINE);
        List<AgentDTO> result = new ArrayList<>();
        for (Agent agent : agents) {
            result.add(AgentDTO.fromEntity(agent));
        }
        return result;
    }

    /**
     * Create new agent
     */
    @Transactional
    public AgentDTO createAgent(CreateAgentRequest request) {
        // Check if agent already exists
        Optional<Agent> existing = agentRepository.findByEmail(request.getEmail());
        if (existing.isPresent()) {
            return AgentDTO.fromEntity(existing.get());
        }

        Agent agent = new Agent();
        agent.setName(request.getName());
        agent.setEmail(request.getEmail());
        agent.setAvatarUrl(request.getAvatarUrl());
        agent.setStatus(AgentStatus.ONLINE);
        agent.setLastActive(LocalDateTime.now());

        return AgentDTO.fromEntity(agentRepository.save(agent));
    }

    /**
     * Update agent status
     */
    @Transactional
    public AgentDTO updateStatus(UUID agentId, AgentStatus status) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found"));
        
        agent.setStatus(status);
        agent.setLastActive(LocalDateTime.now());
        
        return AgentDTO.fromEntity(agentRepository.save(agent));
    }

    /**
     * Increment active conversation count
     */
    @Transactional
    public void incrementActiveConversations(UUID agentId) {
        Optional<Agent> agentOpt = agentRepository.findById(agentId);
        if (agentOpt.isPresent()) {
            Agent agent = agentOpt.get();
            agent.setActiveConversations(agent.getActiveConversations() + 1);
            agentRepository.save(agent);
        }
    }

    /**
     * Decrement active conversation count
     */
    @Transactional
    public void decrementActiveConversations(UUID agentId) {
        Optional<Agent> agentOpt = agentRepository.findById(agentId);
        if (agentOpt.isPresent()) {
            Agent agent = agentOpt.get();
            int count = agent.getActiveConversations();
            agent.setActiveConversations(Math.max(0, count - 1));
            agentRepository.save(agent);
        }
    }

    /**
     * Increment resolved count
     */
    @Transactional
    public void incrementResolvedCount(UUID agentId) {
        Optional<Agent> agentOpt = agentRepository.findById(agentId);
        if (agentOpt.isPresent()) {
            Agent agent = agentOpt.get();
            agent.setTotalResolved(agent.getTotalResolved() + 1);
            agentRepository.save(agent);
        }
    }

    /**
     * Count online agents
     */
    public long countOnlineAgents() {
        return agentRepository.countOnlineAgents();
    }

    /**
     * Find agent entity by ID
     */
    public Optional<Agent> findById(UUID id) {
        return agentRepository.findById(id);
    }
}
