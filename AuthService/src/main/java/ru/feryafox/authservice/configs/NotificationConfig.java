package ru.feryafox.authservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.NotificationService;
import ru.feryafox.kafka.models.NotificationEvent;

@Configuration
public class NotificationConfig {
    @Bean
    public NotificationService notificationService(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        return new NotificationService(kafkaTemplate);
    }
}
