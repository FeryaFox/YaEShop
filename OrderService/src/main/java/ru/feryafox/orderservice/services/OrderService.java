package ru.feryafox.orderservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.feryafox.kafka.models.OrderEvent;
import ru.feryafox.orderservice.entities.Order;
import ru.feryafox.orderservice.entities.ProductItem;
import ru.feryafox.orderservice.repsositories.OrderRepository;
import ru.feryafox.orderservice.repsositories.ProductItemRepository;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductItemRepository productItemRepository;

    @Transactional
    public void createOrder(OrderEvent orderEvent) {
        Order order = Order.builder()
                .id(UUID.fromString(orderEvent.getOrderId()))
                .userId(UUID.fromString(orderEvent.getUserId()))
                .orderStatus(Order.OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        Set<ProductItem> productItems = ProductItem.createProductsFromEvent(orderEvent.getProductItems(), order);
        order.setProductItems(productItems);

        orderRepository.save(order);

        System.out.println("Order created: " + orderEvent.getOrderId());
    }
}
