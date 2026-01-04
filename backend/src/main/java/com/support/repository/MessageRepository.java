package com.support.repository;

import com.support.model.Message;
import com.support.model.Message.SenderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findByConversationIdOrderByCreatedAtAsc(UUID conversationId);

    Page<Message> findByConversationId(UUID conversationId, Pageable pageable);

    List<Message> findBySenderType(SenderType senderType);

    @Query("SELECT m FROM Message m WHERE " +
           "LOWER(m.content) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Message> searchMessages(@Param("query") String query, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId AND m.isRead = false")
    List<Message> findUnreadMessages(@Param("conversationId") UUID conversationId);

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.conversation.id = :conversationId AND m.senderType = 'CUSTOMER'")
    void markAllAsReadForConversation(@Param("conversationId") UUID conversationId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.id = :conversationId AND m.isRead = false AND m.senderType = 'CUSTOMER'")
    long countUnreadMessages(@Param("conversationId") UUID conversationId);

    @Query("SELECT m FROM Message m WHERE m.createdAt >= :since ORDER BY m.createdAt DESC")
    List<Message> findRecentMessages(@Param("since") LocalDateTime since);

    @Query("SELECT m FROM Message m WHERE m.conversation.customer.id = :customerId ORDER BY m.createdAt DESC")
    List<Message> findByCustomerId(@Param("customerId") UUID customerId);
}

