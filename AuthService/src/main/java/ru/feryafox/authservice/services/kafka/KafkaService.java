package ru.feryafox.authservice.services.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.feryafox.authservice.entities.User;
import ru.feryafox.kafka.models.UserEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaService {
    private final KafkaProducerService kafkaProducerService;

    public void sendRegisterUser(User user) {
        try {
            UserEvent userEvent = user.toUserEvent(UserEvent.Status.CREATED);
            log.info("Формируем событие регистрации пользователя: {}", userEvent);

            kafkaProducerService.sendUserEvent(userEvent);

            log.info("Событие регистрации пользователя отправлено в Kafka: {}", userEvent);
        } catch (Exception e) {
            log.error("Ошибка при отправке события регистрации пользователя в Kafka: {}", user.getId(), e);
        }
    }
}
