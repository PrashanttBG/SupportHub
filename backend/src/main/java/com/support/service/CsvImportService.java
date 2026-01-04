package com.support.service;

import com.opencsv.CSVReader;
import com.support.model.*;
import com.support.model.Conversation.ConversationStatus;
import com.support.model.Message.SenderType;
import com.support.repository.*;
import com.support.service.UrgencyDetectionService.UrgencyResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service to import customer messages from CSV file on startup.
 * Also creates default agents and canned messages.
 */
@Service
public class CsvImportService implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AgentRepository agentRepository;
    private final CannedMessageRepository cannedMessageRepository;
    private final UrgencyDetectionService urgencyDetectionService;
    private final ResourceLoader resourceLoader;

    @Value("${app.csv.file-path:classpath:data/GeneralistRails_Project_MessageData.csv}")
    private String csvFilePath;

    // Constructor injection
    public CsvImportService(CustomerRepository customerRepository,
                           ConversationRepository conversationRepository,
                           MessageRepository messageRepository,
                           AgentRepository agentRepository,
                           CannedMessageRepository cannedMessageRepository,
                           UrgencyDetectionService urgencyDetectionService,
                           ResourceLoader resourceLoader) {
        this.customerRepository = customerRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.agentRepository = agentRepository;
        this.cannedMessageRepository = cannedMessageRepository;
        this.urgencyDetectionService = urgencyDetectionService;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void run(String... args) {
        // Only initialize if database is empty
        if (conversationRepository.count() == 0) {
            System.out.println("Initializing database with sample data...");
            createAgents();
            createCannedMessages();
            importCsvData();
        }
    }

    /**
     * Create default support agents
     */
    private void createAgents() {
        Agent agent1 = new Agent();
        agent1.setName("Prashant Baghel");
        agent1.setEmail("prashant@support.com");
        agent1.setStatus(Agent.AgentStatus.ONLINE);
        agent1.setLastActive(LocalDateTime.now());
        agentRepository.save(agent1);

        Agent agent2 = new Agent();
        agent2.setName("Sneha Singh");
        agent2.setEmail("sneha@support.com");
        agent2.setStatus(Agent.AgentStatus.ONLINE);
        agent2.setLastActive(LocalDateTime.now());
        agentRepository.save(agent2);

        Agent agent3 = new Agent();
        agent3.setName("Rajesh Kumar");
        agent3.setEmail("rajesh@support.com");
        agent3.setStatus(Agent.AgentStatus.AWAY);
        agent3.setLastActive(LocalDateTime.now().minusMinutes(30));
        agentRepository.save(agent3);
    }

    /**
     * Create canned response templates
     */
    private void createCannedMessages() {
        String[][] templates = {
            {"Greeting", "Greetings", "Hello! Thank you for contacting us. How can I assist you today?", "/greet"},
            {"Loan Status", "Loan", "Your loan application is being processed. You should receive an update within 2-3 business days.", "/loanstatus"},
            {"Payment Help", "Payment", "I apologize for the payment issue. Can you confirm the date and amount?", "/payment"},
            {"Profile Update", "Account", "To update your profile, go to Settings > Profile in the app.", "/profile"},
            {"Thank You", "Closing", "I'm glad I could help! Is there anything else you need?", "/thanks"},
            {"Follow Up", "Closing", "I've noted this and will follow up within 24 hours.", "/followup"}
        };

        for (String[] template : templates) {
            CannedMessage msg = new CannedMessage();
            msg.setTitle(template[0]);
            msg.setCategory(template[1]);
            msg.setContent(template[2]);
            msg.setShortcut(template[3]);
            msg.setIsActive(true);
            cannedMessageRepository.save(msg);
        }
    }

    /**
     * Import customer messages from CSV file
     */
    private void importCsvData() {
        try {
            Resource resource = resourceLoader.getResource(csvFilePath);
            if (!resource.exists()) {
                System.out.println("CSV file not found, creating sample data...");
                createSampleData();
                return;
            }

            CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()));
            List<String[]> records = reader.readAll();
            reader.close();

            // Skip header row, process each record
            Map<String, Customer> customers = new HashMap<>();
            Map<String, Conversation> conversations = new HashMap<>();

            for (int i = 1; i < records.size(); i++) {
                String[] row = records.get(i);
                if (row.length < 3) continue;

                String userId = row[0].trim();
                String messageContent = row[2].trim();

                if (messageContent.isEmpty()) continue;

                // Get or create customer
                Customer customer = customers.get(userId);
                if (customer == null) {
                    customer = new Customer();
                    customer.setName("Customer " + userId);
                    customer.setEmail("user" + userId + "@customer.com");
                    customer.setAccountStatus("ACTIVE");
                    customer.setLoanStatus("ACTIVE");
                    customer.setTotalConversations(0); // Initialize to 0
                    customer = customerRepository.save(customer);
                    customers.put(userId, customer);
                }

                // Analyze message urgency
                UrgencyResult urgency = urgencyDetectionService.analyzeUrgency(messageContent);

                // Get or create conversation
                Conversation conversation = conversations.get(userId);
                if (conversation == null) {
                    conversation = new Conversation();
                    conversation.setCustomer(customer);
                    conversation.setSubject(truncate(messageContent, 50));
                    conversation.setStatus(ConversationStatus.OPEN);
                    conversation.setUrgencyScore(urgency.getScore());
                    conversation.setUrgencyLevel(urgency.getLevel());
                    conversation.setUrgencyReason(urgency.getReason());
                    conversation.setLastMessageAt(LocalDateTime.now());
                    conversation.setLastMessagePreview(truncate(messageContent, 100));
                    conversation.setUnreadCount(1);
                    conversation = conversationRepository.save(conversation);
                    conversations.put(userId, conversation);
                    
                    // Increment customer conversation count
                    customer.setTotalConversations(customer.getTotalConversations() + 1);
                    customerRepository.save(customer);
                } else {
                    // Update existing conversation
                    conversation.setUnreadCount(conversation.getUnreadCount() + 1);
                    conversation.setLastMessagePreview(truncate(messageContent, 100));
                    conversation.setLastMessageAt(LocalDateTime.now());
                    conversationRepository.save(conversation);
                }

                // Create message
                Message message = new Message();
                message.setConversation(conversation);
                message.setSenderId(customer.getId());
                message.setSenderType(SenderType.CUSTOMER);
                message.setSenderName(customer.getName());
                message.setContent(messageContent);
                message.setIsRead(false);
                messageRepository.save(message);
            }

            System.out.println("Imported " + (records.size() - 1) + " messages from CSV");

        } catch (Exception e) {
            System.out.println("Error importing CSV: " + e.getMessage());
            e.printStackTrace();
            createSampleData();
        }
    }

    /**
     * Create sample data if CSV import fails
     */
    private void createSampleData() {
        String[][] samples = {
            {"1", "When will my loan be approved? I've been waiting for 5 days!"},
            {"2", "Hello, what is the status of my loan disbursement?"},
            {"3", "How do I update my phone number in my profile?"},
            {"4", "My payment failed. Please help urgently!"},
            {"5", "I noticed an unauthorized transaction! This looks like fraud!"},
            {"6", "Can you explain how the loan approval process works?"},
            {"7", "Is there a way to extend my loan repayment period?"},
            {"8", "Thank you for approving my loan! When will I receive the money?"}
        };

        for (String[] sample : samples) {
            // Create customer
            Customer customer = new Customer();
            customer.setName("Customer " + sample[0]);
            customer.setEmail("customer" + sample[0] + "@email.com");
            customer.setAccountStatus("ACTIVE");
            customer.setTotalConversations(0);
            customer = customerRepository.save(customer);

            // Analyze urgency
            UrgencyResult urgency = urgencyDetectionService.analyzeUrgency(sample[1]);

            // Create conversation
            Conversation conversation = new Conversation();
            conversation.setCustomer(customer);
            conversation.setSubject(truncate(sample[1], 50));
            conversation.setStatus(ConversationStatus.OPEN);
            conversation.setUrgencyScore(urgency.getScore());
            conversation.setUrgencyLevel(urgency.getLevel());
            conversation.setUrgencyReason(urgency.getReason());
            conversation.setLastMessageAt(LocalDateTime.now());
            conversation.setLastMessagePreview(truncate(sample[1], 100));
            conversation.setUnreadCount(1);
            conversation = conversationRepository.save(conversation);

            // Increment customer conversation count
            customer.setTotalConversations(customer.getTotalConversations() + 1);
            customerRepository.save(customer);

            // Create message
            Message message = new Message();
            message.setConversation(conversation);
            message.setSenderId(customer.getId());
            message.setSenderType(SenderType.CUSTOMER);
            message.setSenderName(customer.getName());
            message.setContent(sample[1]);
            message.setIsRead(false);
            messageRepository.save(message);
        }

        System.out.println("Created " + samples.length + " sample conversations");
    }

    /**
     * Truncate string to specified length
     */
    private String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}
