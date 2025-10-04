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

    public Transaction createTransaction(CreateTransactionDto createTransactionDto) {
        Transaction transaction = new Transaction();
        transaction.setUserId(createTransactionDto.getUserId());
        transaction.setType(createTransactionDto.getType());
        transaction.setAmount(createTransactionDto.getAmount());
        transaction.setCategory(createTransactionDto.getCategory());
        transaction.setDescription(createTransactionDto.getDescription());
        transaction.setTransactionDate(createTransactionDto.getTransactionDate());

        return transactionRepository.save(transaction);
    }
}
