package ru.feryafox.orderservice.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import ru.feryafox.exceptions.ErrorResponse;
import ru.feryafox.orderservice.entities.Order;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testHandleOrderNotFoundException() {
        OrderIsNotExistsException exception = new OrderIsNotExistsException("order-123");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleOrderNotFoundException(exception);

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals("order_not_found", response.getBody().getError());
    }

    @Test
    void testHandleIncorrectStatusChangeException() {
        IncorrectStatusChangeException exception = new IncorrectStatusChangeException(Order.OrderStatus.CREATED, Order.OrderStatus.SHIPPED);
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIncorrectStatusChangeException(exception);

        assertEquals(CONFLICT, response.getStatusCode());
        assertEquals("incorrect_status_change", response.getBody().getError());
    }

    @Test
    void testHandleAuthenticationException() {
        AuthenticationException exception = new AuthenticationException("Authentication failed") {};
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAuthenticationException(exception);

        assertEquals(UNAUTHORIZED, response.getStatusCode());
        assertEquals("authentication_failed", response.getBody().getError());
    }

    @Test
    void testHandleAccessDeniedException() {
        AccessDeniedException exception = new AccessDeniedException("Access denied");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAccessDeniedException(exception);

        assertEquals(FORBIDDEN, response.getStatusCode());
        assertEquals("access_denied", response.getBody().getError());
    }

    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgumentException(exception);

        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals("bad_request", response.getBody().getError());
    }

    @Test
    void testHandleRuntimeException() {
        RuntimeException exception = new RuntimeException("Server error");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleRuntimeException(exception);

        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("server_error", response.getBody().getError());
    }
}
