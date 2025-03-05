package ru.feryafox.notificationservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.feryafox.notificationservice.entities.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Override
    Optional<User> findById(UUID uuid);
}