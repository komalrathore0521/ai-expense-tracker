package com.expensetracker.user_service.service;

import com.expensetracker.user_service.config.KafkaTopicConfig;
import com.expensetracker.user_service.dto.UserCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    // Spring Boot automatically configures this KafkaTemplate for us.
    // We just need to inject it into our service.
    public KafkaProducerService(KafkaTemplate<String, UserCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sends a UserCreatedEvent to the user-created-topic.
     * @param event The event payload to send.
     */
    public void sendUserCreatedEvent(UserCreatedEvent event) {
        try {
            log.info("Sending UserCreatedEvent to Kafka topic: {}. Event: {}", KafkaTopicConfig.USER_CREATED_TOPIC, event);
            // The send method sends the message asynchronously.
            kafkaTemplate.send(KafkaTopicConfig.USER_CREATED_TOPIC, event);
        } catch (Exception e) {
            log.error("Failed to send UserCreatedEvent to Kafka", e);
        }
    }
}

