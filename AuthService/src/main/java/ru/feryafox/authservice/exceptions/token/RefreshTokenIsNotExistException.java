package ru.feryafox.authservice.exceptions.token;

public class RefreshTokenIsNotExistException extends RuntimeException {
    public RefreshTokenIsNotExistException(String message) {
        super(message);
    }
}
