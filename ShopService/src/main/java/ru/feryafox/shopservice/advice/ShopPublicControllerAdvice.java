package ru.feryafox.shopservice.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.feryafox.shopservice.exceptions.ShopNotFound;

@ControllerAdvice
public class ShopPublicControllerAdvice {
    @ExceptionHandler(ShopNotFound.class)
    public ResponseEntity<?> handleShopNotFound(ShopNotFound exception) {
        return new ResponseEntity<>(ShopNotFound.MESSAGE, HttpStatus.NOT_FOUND);
    }
}
