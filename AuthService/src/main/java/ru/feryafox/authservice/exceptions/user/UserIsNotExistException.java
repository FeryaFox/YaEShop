package ru.feryafox.authservice.exceptions.user;

public class UserIsNotExistException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Пользователь с данным телефоном %s не существует";
    public UserIsNotExistException(String phone) {
      super(String.format(MESSAGE_TEMPLATE, phone));
    }
}
