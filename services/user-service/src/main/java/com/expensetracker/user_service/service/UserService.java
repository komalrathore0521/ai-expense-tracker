package com.expensetracker.user_service.service;

import com.expensetracker.user_service.dto.AuthResponse;
import com.expensetracker.user_service.dto.LoginRequest;
import com.expensetracker.user_service.dto.UserCreatedEvent;
import com.expensetracker.user_service.dto.UserRegistrationDto;
import com.expensetracker.user_service.model.User;
import com.expensetracker.user_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final KafkaProducerService kafkaProducerService; // <<< NEW DEPENDENCY

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, KafkaProducerService kafkaProducerService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.kafkaProducerService = kafkaProducerService; // <<< INJECTED HERE
    }

    @Transactional // Ensures that saving the user and sending the event happen in one transaction
    public User registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already in use.");
        }
        if (userRepository.findByUsername(registrationDto.getUsername()).isPresent()) {
            throw new IllegalStateException("Username already taken.");
        }

        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        // 1. Save the user to the database
        User savedUser = userRepository.save(user);
        log.info("User saved to database with ID: {}", savedUser.getId());

        // 2. --- NEW: Send an event to Kafka ---
        // After the user is successfully saved, create and publish an event
        UserCreatedEvent event = new UserCreatedEvent(savedUser.getId(), savedUser.getEmail(), savedUser.getUsername());
        kafkaProducerService.sendUserCreatedEvent(event);
        // --- END NEW ---

        return savedUser;
    }

    public AuthResponse loginUser(LoginRequest loginRequest) {
        log.info("Attempting login for email: {}", loginRequest.getEmail());
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if (userOptional.isPresent()) {
            log.info("User found in DB. Stored HASH: {}", userOptional.get().getPassword());
        } else {
            log.warn("Login attempt for non-existent user: {}", loginRequest.getEmail());
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        UserDetails userDetails = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));

        String jwtToken = jwtService.generateToken(userDetails);
        return new AuthResponse(jwtToken);
    }
}

