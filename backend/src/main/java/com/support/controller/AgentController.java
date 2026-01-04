package com.support.controller;

import com.support.dto.AgentDTO;
import com.support.dto.request.CreateAgentRequest;
import com.support.model.Agent.AgentStatus;
import com.support.service.AgentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for agent operations
 */
@RestController
@RequestMapping("/api/agents")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    /**
     * Get all agents
     */
    @GetMapping
    public ResponseEntity<List<AgentDTO>> getAllAgents() {
        return ResponseEntity.ok(agentService.getAllAgents());
    }

    /**
     * Get agent by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<AgentDTO> getAgentById(@PathVariable UUID id) {
        return agentService.getAgentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get online agents
     */
    @GetMapping("/online")
    public ResponseEntity<List<AgentDTO>> getOnlineAgents() {
        return ResponseEntity.ok(agentService.getOnlineAgents());
    }

    /**
     * Create new agent
     */
    @PostMapping
    public ResponseEntity<AgentDTO> createAgent(@RequestBody CreateAgentRequest request) {
        return ResponseEntity.ok(agentService.createAgent(request));
    }

    /**
     * Update agent status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<AgentDTO> updateStatus(
            @PathVariable UUID id,
            @RequestParam AgentStatus status) {
        return ResponseEntity.ok(agentService.updateStatus(id, status));
    }

    /**
     * Get agent statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("onlineAgents", agentService.countOnlineAgents());
        return ResponseEntity.ok(stats);
    }
}
