package com.expensetracker.transaction_service.service;

import com.expensetracker.transaction_service.dto.CreateTransactionDto;
import com.expensetracker.transaction_service.model.Transaction;
import com.expensetracker.transaction_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // CREATE
    public Transaction createTransaction(CreateTransactionDto createTransactionDto, Long userId) {
        Transaction transaction = new Transaction();
        transaction.setUserId(userId); // Set from the validated token context
        // Corrected to use getter methods
        transaction.setType(createTransactionDto.getType());
        transaction.setAmount(createTransactionDto.getAmount());
        transaction.setCategory(createTransactionDto.getCategory());
        transaction.setDescription(createTransactionDto.getDescription());
        transaction.setTransactionDate(createTransactionDto.getTransactionDate());

        return transactionRepository.save(transaction);
    }

    // --- NEW METHODS START HERE ---

    // READ (Get all transactions for a specific user)
    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    // READ (Get a single transaction by its ID)
    public Transaction getTransactionById(Long transactionId, Long userId) {
        return transactionRepository.findById(transactionId)
                // This filter is a crucial security check. It ensures that the transaction
                // being requested actually belongs to the user who is currently logged in.
                .filter(transaction -> transaction.getUserId().equals(userId))
                .orElseThrow(() -> new NoSuchElementException("Transaction not found or you do not have permission to view it."));
    }

    // UPDATE
    public Transaction updateTransaction(Long transactionId, CreateTransactionDto dto, Long userId) {
        // First, get the transaction. This re-uses the getTransactionById method,
        // which includes our critical security check.
        Transaction existingTransaction = getTransactionById(transactionId, userId);

        // Update the fields from the DTO using getter methods
        existingTransaction.setAmount(dto.getAmount());
        existingTransaction.setType(dto.getType());
        existingTransaction.setDescription(dto.getDescription());
        existingTransaction.setCategory(dto.getCategory());
        existingTransaction.setTransactionDate(dto.getTransactionDate());

        return transactionRepository.save(existingTransaction);
    }

    // DELETE
    public void deleteTransaction(Long transactionId, Long userId) {
        // We call getTransactionById first to ensure the user owns this transaction
        // before we allow them to delete it.
        Transaction existingTransaction = getTransactionById(transactionId, userId);
        transactionRepository.delete(existingTransaction);
    }
}

