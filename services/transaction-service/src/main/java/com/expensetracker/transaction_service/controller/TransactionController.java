package com.expensetracker.transaction_service.controller;

import com.expensetracker.transaction_service.dto.CreateTransactionDto;
import com.expensetracker.transaction_service.model.Transaction;
import com.expensetracker.transaction_service.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/status")
    public String getStatus() {
        return "Transaction Service is up and running!";
    }

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody CreateTransactionDto createTransactionDto) {
        // Get the authentication object from the security context, which was set by the JwtAuthFilter
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // The 'name' of the principal is the user's email, which we extracted from the token
        String userEmail = authentication.getName();
        log.info("Creating transaction for user: {}", userEmail);

        // --- IMPORTANT LIMITATION FOR NOW ---
        // At this point, the transaction-service only knows the user's email. It doesn't know their unique ID.
        // The proper solution is for the user-service to publish user data to a Kafka topic that this service can read.
        // We will implement that inter-service communication later.
        // For now, we will use a placeholder ID to make the endpoint functional.
        Long userId = 1L; // FAKE USER ID - TO BE REPLACED LATER

        try {
            Transaction newTransaction = transactionService.createTransaction(createTransactionDto, userId);
            return new ResponseEntity<>(newTransaction, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating transaction for user {}: {}", userEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}

