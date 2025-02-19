package ru.feryafox.authservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.feryafox.authservice.entities.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
}