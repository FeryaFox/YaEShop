package ru.feryafox.notificationservice.servicies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.feryafox.kafka.models.NotificationEvent;
import ru.feryafox.notificationservice.entities.Notification;
import ru.feryafox.notificationservice.entities.User;
import ru.feryafox.notificationservice.exceptions.UserIsNotExistsException;
import ru.feryafox.notificationservice.repositories.NotificationRepository;
import ru.feryafox.notificationservice.repositories.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void sendNotification(NotificationEvent notificationEvent) {
        if (notificationEvent.getUserId() == null || notificationEvent.getUserId().isEmpty()) {
            log.error("Ошибка: UserId в событии уведомления отсутствует.");
            return;
        }

        UUID userId;
        try {
            userId = UUID.fromString(notificationEvent.getUserId());
        } catch (IllegalArgumentException e) {
            log.error("Ошибка: Некорректный формат UUID для UserId: {}", notificationEvent.getUserId(), e);
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с ID {} не найден, уведомление не отправлено.", userId);
                    return new UserIsNotExistsException(notificationEvent.getUserId());
                });

        log.info("Отправка на номер {} сообщения: \"{}\"", user.getPhoneNumber(), notificationEvent.getMessage());

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(notificationEvent.getMessage());
        notificationRepository.save(notification);

        log.info("Уведомление успешно сохранено в базе для пользователя {}", user.getPhoneNumber());
    }
}
