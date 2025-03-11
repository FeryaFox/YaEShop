package ru.feryafox.shopservice.exceptions;

import feign.FeignException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import ru.feryafox.exceptions.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ShopNotFound.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Магазин не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ErrorResponse> handleShopNotFoundException(ShopNotFound ex) {
        log.warn("Ошибка: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "shop_not_found", ex.getMessage());
    }

    @ExceptionHandler(NoAccessToShop.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "Нет доступа к магазину",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ErrorResponse> handleNoAccessToShopException(NoAccessToShop ex) {
        log.warn("Ошибка доступа: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "access_denied", "У вас нет доступа к этому магазину");
    }

    @ExceptionHandler(AuthenticationException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Ошибка аутентификации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Ошибка аутентификации: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "authentication_failed", "Неверные учетные данные");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "Ошибка доступа",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Ошибка доступа: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "access_denied", "У вас нет прав для выполнения этой операции");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Ошибка валидации запроса: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return new ResponseEntity<>(new ErrorResponse("validation_error", errors.toString()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Ошибка ConstraintViolationException",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Ошибка валидации: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "validation_error", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Некорректный запрос: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "bad_request", ex.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "502", description = "Ошибка Feign-клиента",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException ex) {
        log.error("Ошибка Feign-клиента: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.BAD_GATEWAY, "feign_error", "Ошибка при запросе внешнего сервиса");
    }

    @ExceptionHandler(RuntimeException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Внутренняя ошибка сервера: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "server_error", "Произошла ошибка на сервере");
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String error, String message) {
        return new ResponseEntity<>(new ErrorResponse(error, message), status);
    }
}
