package ru.feryafox.authservice.exceptions;

import javax.naming.AuthenticationException;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
