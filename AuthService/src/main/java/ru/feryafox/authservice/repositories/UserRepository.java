package ru.feryafox.authservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.feryafox.authservice.entities.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByPhoneNumber(String phoneNumber);
}