package com.expensetracker.budgeting_service.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @GetMapping("/status")
    public String getStatus() {
        return "Budgeting Service is up and running!";
    }
}

