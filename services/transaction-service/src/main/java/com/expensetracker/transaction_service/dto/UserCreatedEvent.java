package com.expensetracker.transaction_service.dto;

/**
 * This DTO represents the incoming "user created" event from Kafka.
 * Its structure MUST EXACTLY MATCH the UserCreatedEvent DTO in the user-service
 * for automatic JSON deserialization to work.
 */
public record UserCreatedEvent(Long id, String email, String username) {
}
