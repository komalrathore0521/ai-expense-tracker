package com.expensetracker.transaction_service.dto;

import com.expensetracker.transaction_service.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;

// This DTO defines the JSON structure we expect for creating a new transaction.
public class CreateTransactionDto {

    private Long userId; // For now, we'll send this directly. Later, we'll get it from the JWT.
    private TransactionType type;
    private BigDecimal amount;
    private String category;
    private String description;
    private LocalDate transactionDate;

    // Getters and Setters are required for Jackson to map the JSON to this object

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }
}
