package com.support.repository;

import com.support.model.Conversation;
import com.support.model.Conversation.ConversationStatus;
import com.support.model.Conversation.UrgencyLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    List<Conversation> findByCustomerId(UUID customerId);

    List<Conversation> findByAssignedAgentId(UUID agentId);

    List<Conversation> findByStatus(ConversationStatus status);

    List<Conversation> findByUrgencyLevel(UrgencyLevel urgencyLevel);

    @Query("SELECT c FROM Conversation c WHERE c.status IN :statuses ORDER BY c.urgencyScore DESC, c.lastMessageAt DESC")
    List<Conversation> findOpenConversationsOrderByUrgency(@Param("statuses") List<ConversationStatus> statuses);

    @Query("SELECT c FROM Conversation c WHERE c.assignedAgent IS NULL AND c.status = 'OPEN' ORDER BY c.urgencyScore DESC")
    List<Conversation> findUnassignedConversations();

    @Query("SELECT c FROM Conversation c WHERE c.assignedAgent.id = :agentId AND c.status IN ('OPEN', 'IN_PROGRESS', 'WAITING')")
    List<Conversation> findActiveConversationsByAgent(@Param("agentId") UUID agentId);

    Page<Conversation> findByStatusIn(List<ConversationStatus> statuses, Pageable pageable);

    @Query("SELECT c FROM Conversation c WHERE " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:urgencyLevel IS NULL OR c.urgencyLevel = :urgencyLevel) AND " +
           "(:agentId IS NULL OR c.assignedAgent.id = :agentId) " +
           "ORDER BY c.urgencyScore DESC, c.lastMessageAt DESC")
    Page<Conversation> findWithFilters(
            @Param("status") ConversationStatus status,
            @Param("urgencyLevel") UrgencyLevel urgencyLevel,
            @Param("agentId") UUID agentId,
            Pageable pageable);

    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.status IN ('OPEN', 'IN_PROGRESS')")
    long countActiveConversations();

    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.urgencyLevel = 'HIGH' OR c.urgencyLevel = 'CRITICAL'")
    long countUrgentConversations();

    // Load conversation with messages - using entity graph approach
    @Query("SELECT DISTINCT c FROM Conversation c LEFT JOIN FETCH c.messages WHERE c.id = :id")
    Optional<Conversation> findByIdWithMessages(@Param("id") UUID id);
}
