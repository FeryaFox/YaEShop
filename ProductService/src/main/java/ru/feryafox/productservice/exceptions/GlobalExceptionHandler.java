package ru.feryafox.productservice.exceptions;

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

    @ExceptionHandler(ShopIsNotExist.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Магазин не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ErrorResponse> handleShopNotFoundException(ShopIsNotExist ex) {
        log.warn("Ошибка: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "shop_not_found", ex.getMessage());
    }

    @ExceptionHandler(ProductIsNotExist.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Продукт не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductIsNotExist ex) {
        log.warn("Ошибка: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "product_not_found", ex.getMessage());
    }

    @ExceptionHandler(ShopAndProductDontLinkedException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Продукт не привязан к магазину",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ErrorResponse> handleShopAndProductDontLinkedException(ShopAndProductDontLinkedException ex) {
        log.warn("Ошибка: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "shop_product_link_error", ex.getMessage());
    }

    @ExceptionHandler(NoAccessToTheShopException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "Нет доступа к магазину",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ErrorResponse> handleNoAccessToShopException(NoAccessToTheShopException ex) {
        log.warn("Ошибка доступа: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "no_access_to_shop", ex.getMessage());
    }

    @ExceptionHandler(NoAccessToTheProductException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "Нет доступа к продукту",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ErrorResponse> handleNoAccessToProductException(NoAccessToTheProductException ex) {
        log.warn("Ошибка доступа: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "no_access_to_product", ex.getMessage());
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
