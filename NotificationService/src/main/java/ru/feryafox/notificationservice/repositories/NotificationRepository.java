package ru.feryafox.notificationservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.feryafox.notificationservice.entities.Notification;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
}