package com.expensetracker.transaction_service.service;

import com.expensetracker.transaction_service.dto.UserCreatedEvent;
import com.expensetracker.transaction_service.model.User;
import com.expensetracker.transaction_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final UserRepository userRepository;

    public KafkaConsumerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Listens for messages on the "user-created-topic".
     * This method will be automatically invoked by Spring Kafka whenever a new message arrives.
     * @param event The deserialized UserCreatedEvent from the Kafka message.
     */
    @KafkaListener(topics = "user-created-topic", groupId = "transaction-group")
    public void consumeUserCreatedEvent(UserCreatedEvent event) {
        log.info("Received UserCreatedEvent: {}", event);

        try {
            User user = new User();
            user.setId(event.id());
            user.setEmail(event.email());
            user.setUsername(event.username());

            userRepository.save(user);
            log.info("Saved new user replica to the database with ID: {}", user.getId());
        } catch (Exception e) {
            log.error("Error processing UserCreatedEvent: {}", event, e);
        }
    }
}
