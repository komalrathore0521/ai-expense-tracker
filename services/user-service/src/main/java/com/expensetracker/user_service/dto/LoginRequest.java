package com.expensetracker.user_service.dto;


// This class models the data we expect in a login request body
public class LoginRequest {
    private String email;
    private String password;

    // Getters and setters are needed for Jackson to deserialize the JSON
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}