package ru.feryafox.cartservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feryafox.cartservice.entities.Cart;
import ru.feryafox.cartservice.entities.CartItem;
import ru.feryafox.cartservice.repositories.CartRepository;
import ru.feryafox.cartservice.repositories.ProductRepository;
import ru.feryafox.models.internal.requests.AddToCartRequest;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CartService {
    private final BaseService baseService;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public void addToCart(String userId, int quantity, AddToCartRequest request) {
        var cart = baseService.getOrCreateCartByUserId(userId);

        System.out.println(cart.getItems());

        var existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            cart.getItems().add(new CartItem(request.getProductId(), quantity));
        }

        // TODO сделать проверку, что если нету продукта, то сделать синхронный запрос
        var product = productRepository.findById(request.getProductId());
        product.get().setPrice(BigDecimal.valueOf(request.getPrice()));
        productRepository.save(product.get());

        cartRepository.save(cart);
    }
}
