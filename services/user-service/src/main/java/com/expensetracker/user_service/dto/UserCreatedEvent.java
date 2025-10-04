package com.expensetracker.user_service.dto;

/**
 * This record represents the data payload for a "user created" event.
 * Using a Java 'record' automatically creates an immutable class with fields,
 * a constructor, getters, equals(), hashCode(), and toString() methods.
 * It's the perfect modern choice for a simple Data Transfer Object (DTO).
 */
public record UserCreatedEvent(Long id, String email, String username) {
}
