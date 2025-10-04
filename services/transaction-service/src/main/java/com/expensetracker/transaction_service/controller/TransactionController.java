package com.expensetracker.transaction_service.controller;

import com.expensetracker.transaction_service.dto.CreateTransactionDto;
import com.expensetracker.transaction_service.model.Transaction;
import com.expensetracker.transaction_service.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

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
    public ResponseEntity<Transaction> createTransaction(@RequestBody CreateTransactionDto createTransactionDto) {
        Transaction newTransaction = transactionService.createTransaction(createTransactionDto);
        return new ResponseEntity<>(newTransaction, HttpStatus.CREATED);
    }
}

