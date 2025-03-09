package ru.feryafox.authservice.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthServiceException extends RuntimeException {
    public AuthServiceException(String message) {
        super(message);
        log.error("Ошибка AuthService: {}", message);
    }
}
