package ru.feryafox.notificationservice.exceptions;

public class UserIsNotExistsException extends NotificationException{
    public UserIsNotExistsException(String userId) {
        super("User with id " + userId + " is not exists");
    }
}
