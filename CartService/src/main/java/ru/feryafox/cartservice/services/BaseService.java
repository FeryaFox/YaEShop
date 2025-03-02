package ru.feryafox.cartservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feryafox.cartservice.entities.Cart;
import ru.feryafox.cartservice.entities.Product;
import ru.feryafox.cartservice.repositories.CartRepository;

@Service
@RequiredArgsConstructor
public class BaseService {
    private final CartRepository cartRepository;

    public Cart getOrCreateCartByUserId(String userId) {
        return cartRepository.findByUserId(userId).orElse(Cart.builder().userId(userId).build());
    }

    public Cart getCartOrNullByUserId(String userId) {
        return cartRepository.findByUserId(userId).orElse(null);
    }
}
