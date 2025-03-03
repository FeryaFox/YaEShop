package ru.feryafox.cartservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feryafox.cartservice.entities.CartItem;
import ru.feryafox.cartservice.exceptions.NoCartException;
import ru.feryafox.cartservice.exceptions.ProductInTheCartIsNotExistException;
import ru.feryafox.cartservice.models.requests.PatchCartRequest;
import ru.feryafox.cartservice.models.responses.CartInfoResponse;
import ru.feryafox.cartservice.repositories.CartRepository;
import ru.feryafox.cartservice.repositories.ProductRepository;
import ru.feryafox.cartservice.services.kafka.KafkaService;
import ru.feryafox.models.internal.requests.AddToCartRequest;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final BaseService baseService;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final KafkaService kafkaService;

    public void addToCart(String userId, int quantity, AddToCartRequest request) {
        var cart = baseService.getOrCreateCartByUserId(userId);

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

    public CartInfoResponse getCartInfo(String userId) {
        var cart = baseService.getCartOrNullByUserId(userId);

        if (cart == null) {
            return null;
        }

        var productIds = cart.getItems().stream()
                .map(CartItem::getProductId)
                .collect(Collectors.toList());
        var productsInCart = productRepository.findAllById(productIds);

        var productsResponse = productsInCart.stream()
                .map(product -> {
                    int quantity = cart.getItems().stream()
                            .filter(item -> item.getProductId().equals(product.getId()))
                            .map(CartItem::getQuantity)
                            .findFirst()
                            .orElse(0);

                    return CartInfoResponse.CartItemResponse.from(product, quantity);
                })
                .toList();

        double cartTotalPrice = productsResponse.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        return CartInfoResponse.builder()
                .cartPrice(cartTotalPrice)
                .cartItems(new HashSet<>(productsResponse))
                .build();
    }

    public void patchCart(String productId, String userId, PatchCartRequest patchCartRequest) {
        var cart = baseService.getCartOrNullByUserId(userId);

        if (cart == null) throw new NoCartException(userId);

        var existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isEmpty()) {
            throw new ProductInTheCartIsNotExistException(productId, userId);
        }

        existingItem.get().setQuantity(patchCartRequest.getQuantity());
        cartRepository.save(cart);
    }

    public void deleteProductFromCart(String productId, String userId) {
        var cart = baseService.getCartOrNullByUserId(userId);

        if (cart == null) throw new NoCartException(userId);

        cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        cartRepository.save(cart);
    }

    public void clearCart(String userId) {
        var cart = baseService.getCartOrNullByUserId(userId);
        if (cart == null) throw new NoCartException(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public void creteOrder(String userId) {
        var cart = baseService.getCartOrNullByUserId(userId);

        if (cart == null) throw new NoCartException(userId);

        kafkaService.sendOrderEvent(cart);

        clearCart(userId);
    }
}
