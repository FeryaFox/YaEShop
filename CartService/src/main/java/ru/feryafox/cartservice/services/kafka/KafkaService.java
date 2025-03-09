package ru.feryafox.cartservice.services.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.feryafox.cartservice.entities.Cart;
import ru.feryafox.cartservice.entities.CartItem;
import ru.feryafox.kafka.models.OrderEvent;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaService {
    private final KafkaProducerService kafkaProducerService;

    public void sendOrderEvent(Cart cart) {
        if (cart.getItems().isEmpty()) {
            log.warn("Попытка создать заказ для пользователя {} с пустой корзиной. Заказ не отправлен.", cart.getUserId());
            return;
        }

        String orderId = UUID.randomUUID().toString();
        var products = cart.getItems().stream()
                .map(CartItem::toProductItem)
                .collect(Collectors.toSet());

        double totalPrice = products.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        var orderEvent = OrderEvent.builder()
                .orderId(orderId)
                .userId(cart.getUserId())
                .orderStatus(OrderEvent.OrderStatus.CREATED)
                .productItems(products)
                .totalPrice(totalPrice)
                .build();

        log.info("Создан заказ {} для пользователя {}. Итоговая сумма: {}. Отправка в Kafka...", orderId, cart.getUserId(), totalPrice);

        try {
            kafkaProducerService.sendCreateOrder(orderEvent);
            log.info("Заказ {} успешно отправлен в Kafka.", orderId);
        } catch (Exception e) {
            log.error("Ошибка при отправке заказа {} в Kafka: {}", orderId, e.getMessage(), e);
        }
    }
}
