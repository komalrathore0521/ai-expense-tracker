package com.expensetracker.user_service.controller;

import com.expensetracker.user_service.dto.AuthResponse;
import com.expensetracker.user_service.dto.LoginRequest;
import com.expensetracker.user_service.dto.UserRegistrationDto;
import com.expensetracker.user_service.model.User;
import com.expensetracker.user_service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @GetMapping("/status")
    public String getStatus() {
        return "User Service is up and running!";
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        try {
            User user = userService.registerUser(registrationDto);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = userService.loginUser(loginRequest);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            // This new line logs the actual error to your console
            log.error("Authentication failed for user {}: {}", loginRequest.getEmail(), e.getMessage());

            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        // The JwtAuthFilter has already validated the token and set the authentication context.
        // We can safely get the authenticated user's details here.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Create a response map with the user's public information
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", currentUser.getId());
        profile.put("username", currentUser.getUsername()); // This returns the actual username field
        profile.put("email", currentUser.getEmail());
        profile.put("createdAt", currentUser.getCreatedAt());

        return ResponseEntity.ok(profile);
    }
}
