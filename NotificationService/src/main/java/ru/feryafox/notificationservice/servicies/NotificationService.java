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
        User user = userRepository.findById(UUID.fromString(notificationEvent.getUserId())).orElseThrow(() -> new UserIsNotExistsException(notificationEvent.getUserId()));
        log.info("Отправка на номер {} сообщения {}", user.getPhoneNumber(), notificationEvent.getMessage());
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(notificationEvent.getMessage());
        notificationRepository.save(notification);
    }
}
