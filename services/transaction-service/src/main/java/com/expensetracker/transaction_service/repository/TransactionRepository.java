package com.expensetracker.transaction_service.repository;

import com.expensetracker.transaction_service.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Custom query to find all transactions for a specific user
    List<Transaction> findByUserId(Long userId);
}
