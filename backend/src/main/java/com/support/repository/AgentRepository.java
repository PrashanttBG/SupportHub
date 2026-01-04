package com.support.repository;

import com.support.model.Agent;
import com.support.model.Agent.AgentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AgentRepository extends JpaRepository<Agent, UUID> {

    Optional<Agent> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Agent> findByStatus(AgentStatus status);

    @Query("SELECT a FROM Agent a WHERE a.status = 'ONLINE' ORDER BY a.activeConversations ASC")
    List<Agent> findAvailableAgents();

    @Query("SELECT a FROM Agent a ORDER BY a.totalResolved DESC")
    List<Agent> findTopPerformingAgents();

    @Query("SELECT COUNT(a) FROM Agent a WHERE a.status = 'ONLINE'")
    long countOnlineAgents();
}

