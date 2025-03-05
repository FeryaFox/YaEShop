package ru.feryafox.authservice.services.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feryafox.authservice.entities.User;
import ru.feryafox.kafka.models.UserEvent;

@Service
@RequiredArgsConstructor
public class KafkaService {
    private final KafkaProducerService kafkaProducerService;

    public void sendRegisterUser(User user) {
        kafkaProducerService.sendUserEvent(user.toUserEvent(UserEvent.Status.CREATED));
    }
}
