package ru.feryafox.authservice.exceptions.user;

import ru.feryafox.authservice.exceptions.AuthException;

public class UserIsExistException extends AuthException {
    private static final String MESSAGE_TEMPLATE = "Пользователь с данным телефоном %s уже существует";

    public UserIsExistException(String phone) {
        super(String.format(MESSAGE_TEMPLATE, phone));
    }
}
