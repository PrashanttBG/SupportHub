package com.support.repository;

import com.support.model.CannedMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CannedMessageRepository extends JpaRepository<CannedMessage, UUID> {

    List<CannedMessage> findByCategory(String category);

    List<CannedMessage> findByIsActiveTrue();

    List<CannedMessage> findByCategoryAndIsActiveTrue(String category);

    Optional<CannedMessage> findByShortcut(String shortcut);

    @Query("SELECT DISTINCT c.category FROM CannedMessage c WHERE c.isActive = true")
    List<String> findAllCategories();

    @Query("SELECT c FROM CannedMessage c WHERE c.isActive = true ORDER BY c.usageCount DESC")
    List<CannedMessage> findMostUsed();

    @Query("SELECT c FROM CannedMessage c WHERE c.isActive = true AND " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.content) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<CannedMessage> searchCannedMessages(@Param("query") String query);
}

