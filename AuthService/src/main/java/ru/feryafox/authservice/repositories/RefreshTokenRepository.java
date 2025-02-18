package ru.feryafox.authservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.feryafox.authservice.entities.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}