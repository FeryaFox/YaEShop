package ru.feryafox.cartservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.feryafox.cartservice.entities.Cart;
import ru.feryafox.cartservice.repositories.CartRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class BaseService {
    private final CartRepository cartRepository;

    public Cart getOrCreateCartByUserId(String userId) {
        log.info("Запрос корзины для пользователя: {}", userId);

        return cartRepository.findByUserId(userId).orElseGet(() -> {
            log.warn("Корзина для пользователя {} не найдена, создаем новую", userId);
            return Cart.builder()
                    .userId(userId)
                    .build();
        });
    }

    public Cart getCartOrNullByUserId(String userId) {
        log.info("Запрос (без создания) корзины для пользователя: {}", userId);
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            log.warn("Корзина для пользователя {} не найдена", userId);
            return null;
        });
    }
}
