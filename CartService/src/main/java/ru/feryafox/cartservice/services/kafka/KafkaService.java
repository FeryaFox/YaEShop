package ru.feryafox.cartservice.services.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feryafox.cartservice.entities.Cart;
import ru.feryafox.cartservice.entities.CartItem;
import ru.feryafox.kafka.models.OrderEvent;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KafkaService {
    private final KafkaProducerService kafkaProducerService;

    public void sendOrderEvent(Cart cart) {
        var eventBuilder = OrderEvent.builder()
                .orderId(UUID.randomUUID().toString())
                .userId(cart.getUserId())
                .orderStatus(OrderEvent.OrderStatus.CREATED);

        var products = cart.getItems().stream()
                .map(CartItem::toProductItem)
                .collect(Collectors.toSet());

        double totalPrice = products.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        eventBuilder.productItems(products);
        eventBuilder.totalPrice(totalPrice);

        kafkaProducerService.sendCreateOrder(eventBuilder.build());
    }
}
