package ru.feryafox.authservice.exceptions.user;

import ru.feryafox.authservice.exceptions.AuthServiceException;

public class UserIsExistException extends AuthServiceException {
    public UserIsExistException(String phoneNumber) {
        super("Пользователь с номером " + phoneNumber + " уже существует");
    }
}
