package com.expensetracker.transaction_service.service;

import com.expensetracker.transaction_service.dto.CreateTransactionDto;
import com.expensetracker.transaction_service.model.Transaction;
import com.expensetracker.transaction_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // This method now accepts the userId from the controller
    public Transaction createTransaction(CreateTransactionDto createTransactionDto, Long userId) {
        Transaction transaction = new Transaction();
        transaction.setUserId(userId); // Set from the validated token context
        transaction.setType(createTransactionDto.getType());
        transaction.setAmount(createTransactionDto.getAmount());
        transaction.setCategory(createTransactionDto.getCategory());
        transaction.setDescription(createTransactionDto.getDescription());
        transaction.setTransactionDate(createTransactionDto.getTransactionDate());

        return transactionRepository.save(transaction);
    }
}
