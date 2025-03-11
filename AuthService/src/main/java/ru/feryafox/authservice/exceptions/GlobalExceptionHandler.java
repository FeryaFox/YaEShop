package ru.feryafox.authservice.exceptions;

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
import ru.feryafox.authservice.exceptions.token.RefreshTokenIsNotExistException;
import ru.feryafox.authservice.exceptions.user.UserIsExistException;
import ru.feryafox.authservice.exceptions.user.UserIsNotExistException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Пользователь не найден
    @ExceptionHandler(UserIsNotExistException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserIsNotExistException ex) {
        log.warn("Ошибка: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "user_not_found", ex.getMessage());
    }

    // Пользователь уже существует
    @ExceptionHandler(UserIsExistException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExistsException(UserIsExistException ex) {
        log.warn("Ошибка: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, "user_already_exists", ex.getMessage());
    }

    // Рефреш-токен не найден
    @ExceptionHandler(RefreshTokenIsNotExistException.class)
    public ResponseEntity<Map<String, String>> handleRefreshTokenNotFoundException(RefreshTokenIsNotExistException ex) {
        log.warn("Ошибка: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "refresh_token_not_found", ex.getMessage());
    }

    // Ошибки аутентификации
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Ошибка аутентификации: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "authentication_failed", "Неверные учетные данные");
    }

    // Ошибки безопасности
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Ошибка доступа: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "access_denied", "У вас нет прав для выполнения этой операции");
    }

    // Ошибки в JWT-токене
    @ExceptionHandler({JwtException.class, ExpiredJwtException.class})
    public ResponseEntity<Map<String, String>> handleJwtException(JwtException ex) {
        log.warn("Ошибка JWT-токена: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "invalid_token", "Некорректный или просроченный токен");
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

    // Ошибки ConstraintViolationException (например, валидация номера телефона)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Ошибка валидации: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "validation_error", ex.getMessage());
    }

    // Некорректные запросы (например, неверный UUID)
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
