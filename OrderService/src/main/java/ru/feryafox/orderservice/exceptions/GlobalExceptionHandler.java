package ru.feryafox.orderservice.exceptions;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.feryafox.orderservice.exceptions.IncorrectStatusChangeException;
import ru.feryafox.orderservice.exceptions.OrderIsNotExistsException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Ошибка: заказ не найден
    @ExceptionHandler(OrderIsNotExistsException.class)
    public ResponseEntity<Map<String, String>> handleOrderNotFoundException(OrderIsNotExistsException ex) {
        log.warn("Ошибка: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "order_not_found", ex.getMessage());
    }

    // Ошибка смены статуса заказа
    @ExceptionHandler(IncorrectStatusChangeException.class)
    public ResponseEntity<Map<String, String>> handleIncorrectStatusChangeException(IncorrectStatusChangeException ex) {
        log.warn("Ошибка: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, "incorrect_status_change", ex.getMessage());
    }

    // Ошибки аутентификации
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Ошибка аутентификации: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "authentication_failed", "Неверные учетные данные");
    }

    // Ошибки доступа
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Ошибка доступа: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "access_denied", "У вас нет прав для выполнения этой операции");
    }

    // Ошибки валидации входных данных
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Ошибка валидации запроса: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Ошибки ConstraintViolationException (например, валидация параметров запроса)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Ошибка валидации: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "validation_error", ex.getMessage());
    }

    // Некорректные запросы (например, неверный формат UUID)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Некорректный запрос: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "bad_request", ex.getMessage());
    }

    // Общие ошибки сервера
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        log.error("Внутренняя ошибка сервера: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "server_error", "Произошла ошибка на сервере");
    }

    // Метод для формирования ответа
    private ResponseEntity<Map<String, String>> buildResponse(HttpStatus status, String error, String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", error);
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }
}
