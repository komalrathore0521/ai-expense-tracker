package com.expensetracker.transaction_service.controller;

import com.expensetracker.transaction_service.dto.CreateTransactionDto;
import com.expensetracker.transaction_service.model.Transaction;
import com.expensetracker.transaction_service.model.User;
import com.expensetracker.transaction_service.repository.UserRepository;
import com.expensetracker.transaction_service.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;
    private final UserRepository userRepository; // <<< NEW DEPENDENCY

    public TransactionController(TransactionService transactionService, UserRepository userRepository) {
        this.transactionService = transactionService;
        this.userRepository = userRepository; // <<< INJECTED HERE
    }

    @GetMapping("/status")
    public String getStatus() {
        return "Transaction Service is up and running!";
    }

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody CreateTransactionDto createTransactionDto) {
        // Get the authentication object, which contains the user's details from the JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        log.info("Received request to create transaction for user email: {}", userEmail);

        try {
            // --- NEW: Look up the real user ID from the local replica ---
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalStateException("User replica not found for email: " + userEmail + ". The user may not have been synchronized via Kafka yet."));

            Long userId = user.getId();
            log.info("Found real user ID: {} for email: {}", userId, userEmail);
            // --- END NEW ---

            Transaction newTransaction = transactionService.createTransaction(createTransactionDto, userId);
            return new ResponseEntity<>(newTransaction, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating transaction for user {}: {}", userEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}
