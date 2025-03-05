package ru.feryafox.authservice.services.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.UserEvent;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    public void sendUserEvent(UserEvent userEvent) {
        kafkaTemplate.send("user-topic", userEvent.getId(), userEvent);
    }
}
