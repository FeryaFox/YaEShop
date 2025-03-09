package ru.feryafox.cartservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final BaseService baseService;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final KafkaService kafkaService;

    public void addToCart(String userId, int quantity, AddToCartRequest request) {
        log.info("Добавление товара в корзину. Пользователь: {}, Товар: {}, Количество: {}", userId, request.getProductId(), quantity);

        var cart = baseService.getOrCreateCartByUserId(userId);
        var existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
            log.info("Количество товара {} обновлено до {}", request.getProductId(), existingItem.get().getQuantity());
        } else {
            var product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> {
                        log.warn("Товар {} не найден в базе данных", request.getProductId());
                        return new RuntimeException("Product not found");
                    });

            cart.getItems().add(new CartItem(request.getProductId(), quantity, product.getPrice(), product.getShopId()));
            log.info("Товар {} добавлен в корзину", request.getProductId());
        }

        cartRepository.save(cart);
    }

    public CartInfoResponse getCartInfo(String userId) {
        log.info("Запрос информации о корзине пользователя: {}", userId);

        var cart = baseService.getCartOrNullByUserId(userId);
        if (cart == null) {
            log.warn("Корзина пользователя {} не найдена", userId);
            return null;
        }

        var productIds = cart.getItems().stream().map(CartItem::getProductId).collect(Collectors.toList());
        var productsInCart = productRepository.findAllById(productIds);

        var productsResponse = productsInCart.stream().map(product -> {
            int quantity = cart.getItems().stream()
                    .filter(item -> item.getProductId().equals(product.getId()))
                    .map(CartItem::getQuantity)
                    .findFirst()
                    .orElse(0);

            return CartInfoResponse.CartItemResponse.from(product, quantity);
        }).toList();

        double cartTotalPrice = productsResponse.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        log.info("Корзина пользователя {} успешно загружена. Общая сумма: {}", userId, cartTotalPrice);

        return CartInfoResponse.builder()
                .cartPrice(cartTotalPrice)
                .cartItems(new HashSet<>(productsResponse))
                .build();
    }

    public void patchCart(String productId, String userId, PatchCartRequest patchCartRequest) {
        log.info("Обновление количества товара {} в корзине пользователя {} на {}", productId, userId, patchCartRequest.getQuantity());

        var cart = baseService.getCartOrNullByUserId(userId);
        if (cart == null) throw new NoCartException(userId);

        var existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ProductInTheCartIsNotExistException(productId, userId));

        existingItem.setQuantity(patchCartRequest.getQuantity());
        cartRepository.save(cart);
        log.info("Количество товара {} в корзине пользователя {} обновлено до {}", productId, userId, patchCartRequest.getQuantity());
    }

    public void deleteProductFromCart(String productId, String userId) {
        log.info("Удаление товара {} из корзины пользователя {}", productId, userId);

        var cart = baseService.getCartOrNullByUserId(userId);
        if (cart == null) throw new NoCartException(userId);

        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        cartRepository.save(cart);

        log.info("Товар {} удален из корзины пользователя {}", productId, userId);
    }

    public void clearCart(String userId) {
        log.info("Очистка корзины пользователя {}", userId);

        var cart = baseService.getCartOrNullByUserId(userId);
        if (cart == null) throw new NoCartException(userId);

        cart.getItems().clear();
        cartRepository.save(cart);

        log.info("Корзина пользователя {} успешно очищена", userId);
    }

    public void createOrder(String userId) {
        log.info("Оформление заказа для пользователя {}", userId);

        var cart = baseService.getCartOrNullByUserId(userId);
        if (cart == null) throw new NoCartException(userId);

        kafkaService.sendOrderEvent(cart);
        clearCart(userId);

        log.info("Заказ для пользователя {} успешно создан и отправлен в Kafka", userId);
    }
}
