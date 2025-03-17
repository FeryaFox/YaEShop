package ru.feryafox.authservice.exceptions.token;

import ru.feryafox.authservice.exceptions.AuthServiceException;

public class RefreshTokenIsNotExistException extends AuthServiceException {
    public RefreshTokenIsNotExistException(String token) {
        super("Рефреш токен не найден: " + token);
    }
}
