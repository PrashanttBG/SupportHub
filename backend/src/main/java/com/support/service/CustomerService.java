package com.support.service;

import com.support.dto.CustomerDTO;
import com.support.model.Customer;
import com.support.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing customers
 */
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Get all customers
     */
    public List<CustomerDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> result = new ArrayList<>();
        for (Customer customer : customers) {
            result.add(CustomerDTO.fromEntity(customer));
        }
        return result;
    }

    /**
     * Get customer by ID
     */
    public Optional<CustomerDTO> getCustomerById(UUID id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            return Optional.of(CustomerDTO.fromEntity(customer.get()));
        }
        return Optional.empty();
    }

    /**
     * Search customers by name or email
     */
    public List<CustomerDTO> searchCustomers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<Customer> customers = customerRepository.searchCustomers(query.trim());
        List<CustomerDTO> result = new ArrayList<>();
        for (Customer customer : customers) {
            result.add(CustomerDTO.fromEntity(customer));
        }
        return result;
    }

    /**
     * Find existing customer or create new one
     */
    @Transactional
    public Customer findOrCreateCustomer(String name, String email, String phone) {
        // Try to find by email first
        if (email != null && !email.isEmpty()) {
            Optional<Customer> existing = customerRepository.findByEmail(email);
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        // Try to find by phone
        if (phone != null && !phone.isEmpty()) {
            Optional<Customer> existing = customerRepository.findByPhone(phone);
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        // Create new customer
        Customer customer = new Customer();
        customer.setName(name != null ? name : "Unknown Customer");
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setAccountStatus("ACTIVE");
        customer.setLoanStatus("N/A");
        customer.setTotalConversations(0); // Initialize to 0

        return customerRepository.save(customer);
    }

    /**
     * Increment conversation count for customer
     */
    @Transactional
    public void incrementConversationCount(UUID customerId) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            // Ensure totalConversations is not null
            if (customer.getTotalConversations() == null) {
                customer.setTotalConversations(0);
            }
            customer.setTotalConversations(customer.getTotalConversations() + 1);
            customerRepository.save(customer);
        }
    }
}
