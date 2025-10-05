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

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;
    private final UserRepository userRepository;

    public TransactionController(TransactionService transactionService, UserRepository userRepository) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }

    // Helper method to get the current user's ID securely
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("User replica not found for email: " + userEmail));
        return user.getId();
    }

    // Helper method for consistent error responses
    private ResponseEntity<Map<String, String>> buildErrorResponse(Exception e, HttpStatus status) {
        log.error("Error processing transaction request: {}", e.getMessage());
        return ResponseEntity.status(status).body(Map.of("error", e.getMessage()));
    }

    @GetMapping("/status")
    public String getStatus() {
        return "Transaction Service is up and running!";
    }

    // CREATE
    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody CreateTransactionDto createTransactionDto) {
        try {
            Long userId = getCurrentUserId();
            Transaction newTransaction = transactionService.createTransaction(createTransactionDto, userId);
            return new ResponseEntity<>(newTransaction, HttpStatus.CREATED);
        } catch (Exception e) {
            return buildErrorResponse(e, HttpStatus.BAD_REQUEST);
        }
    }

    // --- NEW ENDPOINTS START HERE ---

    // READ (Get all transactions for the logged-in user)
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllUserTransactions() {
        Long userId = getCurrentUserId();
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(transactions);
    }

    // READ (Get a single transaction by its ID)
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            Transaction transaction = transactionService.getTransactionById(id, userId);
            return ResponseEntity.ok(transaction);
        } catch (NoSuchElementException e) {
            return buildErrorResponse(e, HttpStatus.NOT_FOUND);
        }
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable Long id, @RequestBody CreateTransactionDto dto) {
        try {
            Long userId = getCurrentUserId();
            Transaction updatedTransaction = transactionService.updateTransaction(id, dto, userId);
            return ResponseEntity.ok(updatedTransaction);
        } catch (NoSuchElementException e) {
            return buildErrorResponse(e, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return buildErrorResponse(e, HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            transactionService.deleteTransaction(id, userId);
            return ResponseEntity.noContent().build(); // 204 No Content is standard for successful delete
        } catch (NoSuchElementException e) {
            return buildErrorResponse(e, HttpStatus.NOT_FOUND);
        }
    }
}

