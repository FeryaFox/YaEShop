package ru.feryafox.notificationservice.servicies.kafka;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.UserEvent;
import ru.feryafox.notificationservice.entities.User;
import ru.feryafox.notificationservice.repositories.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaService {
    private final UserRepository userRepository;

    @Transactional
    public void processUserEvent(UserEvent userEvent) {
        log.info("Получено событие UserEvent с ID: {}, статус: {}", userEvent.getId(), userEvent.getStatus());

        UUID userId;
        try {
            userId = UUID.fromString(userEvent.getId());
        } catch (IllegalArgumentException e) {
            log.error("Ошибка обработки UserEvent: Некорректный UUID: {}", userEvent.getId(), e);
            return;
        }

        switch (userEvent.getStatus()) {
            case CREATED -> handleUserCreated(userEvent, userId);
            case UPDATED -> handleUserUpdated(userEvent, userId);
            default -> log.warn("Неизвестный статус UserEvent: {}", userEvent.getStatus());
        }
    }

    private void handleUserCreated(UserEvent userEvent, UUID userId) {
        log.info("Создание нового пользователя с ID: {}", userId);

        User user = User.builder()
                .id(userId)
                .phoneNumber(userEvent.getPhoneNumber())
                .firstName(userEvent.getFirstName())
                .surname(userEvent.getSurname())
                .middleName(userEvent.getMiddleName())
                .build();

        userRepository.save(user);
        log.info("Пользователь {} успешно создан", userId);
    }

    private void handleUserUpdated(UserEvent userEvent, UUID userId) {
        log.info("Обновление пользователя с ID: {}", userId);

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            log.warn("Ошибка обновления: пользователь с ID {} не найден", userId);
            return;
        }

        User user = optionalUser.get();
        user.setPhoneNumber(userEvent.getPhoneNumber());
        user.setFirstName(userEvent.getFirstName());
        user.setSurname(userEvent.getSurname());
        user.setMiddleName(userEvent.getMiddleName());

        userRepository.save(user);
        log.info("Пользователь {} успешно обновлен", userId);
    }
}
