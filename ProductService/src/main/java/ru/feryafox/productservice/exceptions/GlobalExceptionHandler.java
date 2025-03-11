package ru.feryafox.productservice.exceptions;

import feign.FeignException;
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

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Магазин не найден
    @ExceptionHandler(ShopIsNotExist.class)
    public ResponseEntity<Map<String, String>> handleShopNotFoundException(ShopIsNotExist ex) {
        log.warn("Ошибка: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "shop_not_found", ex.getMessage());
    }

    // Продукт не найден
    @ExceptionHandler(ProductIsNotExist.class)
    public ResponseEntity<Map<String, String>> handleProductNotFoundException(ProductIsNotExist ex) {
        log.warn("Ошибка: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "product_not_found", ex.getMessage());
    }

    // Продукт не принадлежит магазину
    @ExceptionHandler(ShopAndProductDontLinkedException.class)
    public ResponseEntity<Map<String, String>> handleShopAndProductDontLinkedException(ShopAndProductDontLinkedException ex) {
        log.warn("Ошибка: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "shop_product_link_error", ex.getMessage());
    }

    // Нет доступа к магазину
    @ExceptionHandler(NoAccessToTheShopException.class)
    public ResponseEntity<Map<String, String>> handleNoAccessToShopException(NoAccessToTheShopException ex) {
        log.warn("Ошибка доступа: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "no_access_to_shop", ex.getMessage());
    }

    // Нет доступа к продукту
    @ExceptionHandler(NoAccessToTheProductException.class)
    public ResponseEntity<Map<String, String>> handleNoAccessToProductException(NoAccessToTheProductException ex) {
        log.warn("Ошибка доступа: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "no_access_to_product", ex.getMessage());
    }

    // Ошибки при запросах к Feign-клиенту (если магазин или корзина не найдены)
    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<Map<String, String>> handleFeignNotFoundException(FeignException.NotFound ex) {
        log.warn("Ошибка Feign-запроса: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.NOT_FOUND, "feign_not_found", "Запрашиваемый ресурс не найден");
    }

    // Общие ошибки Feign
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, String>> handleFeignException(FeignException ex) {
        log.error("Ошибка Feign-запроса: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "feign_error", "Ошибка при запросе к внешнему сервису");
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

    // Некорректные запросы (например, неверный формат UUID, отрицательные параметры пагинации)
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

    // Метод для формирования JSON-ответа
    private ResponseEntity<Map<String, String>> buildResponse(HttpStatus status, String error, String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", error);
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }
}
