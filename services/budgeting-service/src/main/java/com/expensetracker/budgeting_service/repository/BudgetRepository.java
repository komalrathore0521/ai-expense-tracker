package com.expensetracker.budgeting_service.repository;


import com.expensetracker.budgeting_service.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    // Custom query method to find all budgets for a specific user
    List<Budget> findByUserId(Long userId);
}


