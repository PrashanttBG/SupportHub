package com.support.service;

import com.support.model.Conversation.UrgencyLevel;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Service to detect urgency level of customer messages.
 * Uses simple keyword matching to categorize messages.
 */
@Service
public class UrgencyDetectionService {

    // Keywords that indicate high urgency (loan/payment issues)
    private static final List<String> CRITICAL_KEYWORDS = Arrays.asList(
            "fraud", "unauthorized", "stolen", "hacked", "emergency"
    );

    private static final List<String> HIGH_KEYWORDS = Arrays.asList(
            "urgent", "immediately", "asap", "loan approval", "loan rejected",
            "payment failed", "disbursement", "blocked", "suspended"
    );

    private static final List<String> MEDIUM_KEYWORDS = Arrays.asList(
            "waiting", "pending", "delay", "not received", "when will",
            "how long", "status", "issue", "problem", "help"
    );

    /**
     * Analyzes message content and returns urgency information.
     */
    public UrgencyResult analyzeUrgency(String messageContent) {
        if (messageContent == null || messageContent.trim().isEmpty()) {
            return new UrgencyResult(0, UrgencyLevel.LOW, "No content");
        }

        String content = messageContent.toLowerCase();

        // Check for critical keywords first
        for (String keyword : CRITICAL_KEYWORDS) {
            if (content.contains(keyword)) {
                return new UrgencyResult(95, UrgencyLevel.CRITICAL, "Critical: " + keyword);
            }
        }

        // Check for high urgency keywords
        for (String keyword : HIGH_KEYWORDS) {
            if (content.contains(keyword)) {
                return new UrgencyResult(80, UrgencyLevel.HIGH, "High priority: " + keyword);
            }
        }

        // Check for medium urgency keywords
        for (String keyword : MEDIUM_KEYWORDS) {
            if (content.contains(keyword)) {
                return new UrgencyResult(50, UrgencyLevel.MEDIUM, "Medium priority: " + keyword);
            }
        }

        // Default to low urgency
        return new UrgencyResult(20, UrgencyLevel.LOW, "General inquiry");
    }

    /**
     * Simple class to hold urgency analysis result.
     */
    public static class UrgencyResult {
        private int score;
        private UrgencyLevel level;
        private String reason;

        public UrgencyResult(int score, UrgencyLevel level, String reason) {
            this.score = score;
            this.level = level;
            this.reason = reason;
        }

        public int getScore() {
            return score;
        }

        public UrgencyLevel getLevel() {
            return level;
        }

        public String getReason() {
            return reason;
        }
    }
}
