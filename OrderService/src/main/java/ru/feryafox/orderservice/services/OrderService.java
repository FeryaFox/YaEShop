package ru.feryafox.orderservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.feryafox.kafka.models.OrderEvent;
import ru.feryafox.kafka.models.PaymentResponseEvent;
import ru.feryafox.orderservice.entities.Order;
import ru.feryafox.orderservice.entities.ProductItem;
import ru.feryafox.orderservice.exceptions.IncorrectStatusChangeException;
import ru.feryafox.orderservice.models.requests.ChangeStatusRequest;
import ru.feryafox.orderservice.models.responses.UserOrderInfoResponse;
import ru.feryafox.orderservice.repsositories.OrderRepository;
import ru.feryafox.orderservice.repsositories.ProductItemRepository;
import ru.feryafox.orderservice.services.kafka.KafkaService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductItemRepository productItemRepository;
    private final BaseService baseService;
    private final KafkaService kafkaService;

    @Transactional
    public void createOrder(OrderEvent orderEvent) {
        Order order = Order.builder()
                .id(UUID.fromString(orderEvent.getOrderId()))
                .userId(UUID.fromString(orderEvent.getUserId()))
                .orderStatus(Order.OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .totalPrice(BigDecimal.valueOf(orderEvent.getTotalPrice()))
                .build();

        Set<ProductItem> productItems = ProductItem.createProductsFromEvent(orderEvent.getProductItems(), order);
        order.setProductItems(productItems);

        orderRepository.save(order);

        System.out.println("Order created: " + orderEvent.getOrderId());

        changeStatus(order.getId().toString(), Order.OrderStatus.PENDING_PAYMENT);
    }

    @Transactional
    public void changeStatus(String orderId, Order.OrderStatus newStatus) {
        var order = baseService.getOrderById(UUID.fromString(orderId));

        switch (newStatus) {
            case PENDING_PAYMENT:
                if (!order.getOrderStatus().equals(Order.OrderStatus.CREATED)) throw new IncorrectStatusChangeException(order.getOrderStatus(), newStatus);
                order.setOrderStatus(Order.OrderStatus.PENDING_PAYMENT);

                kafkaService.createPaymentRequest(order);

                break;
            case PAID:
                if (!order.getOrderStatus().equals(Order.OrderStatus.PENDING_PAYMENT)) throw new IncorrectStatusChangeException(order.getOrderStatus(), newStatus);
                order.setOrderStatus(Order.OrderStatus.PAID);

                break;

            case PROCESSING:
                if (!order.getOrderStatus().equals(Order.OrderStatus.PAID)) throw new IncorrectStatusChangeException(order.getOrderStatus(), newStatus);
                order.setOrderStatus(Order.OrderStatus.PROCESSING);
                break;

            case SHIPPED:
                if (!order.getOrderStatus().equals(Order.OrderStatus.PROCESSING)) throw new IncorrectStatusChangeException(order.getOrderStatus(), newStatus);
                order.setOrderStatus(Order.OrderStatus.SHIPPED);
                break;

            case DELIVERED:
                if (!order.getOrderStatus().equals(Order.OrderStatus.SHIPPED)) throw new IncorrectStatusChangeException(order.getOrderStatus(), newStatus);
                order.setOrderStatus(Order.OrderStatus.DELIVERED);
                break;

            case COMPLETED:
                if (!order.getOrderStatus().equals(Order.OrderStatus.DELIVERED)) throw new IncorrectStatusChangeException(order.getOrderStatus(), newStatus);
                order.setOrderStatus(Order.OrderStatus.COMPLETED);
                break;
        }

        orderRepository.save(order);
    }

    @Transactional
    public void processPayment(PaymentResponseEvent paymentResponseEvent) {
        changeStatus(paymentResponseEvent.getOrderId(), Order.OrderStatus.PAID);
    }

    @Transactional
    public void changeOrderStatus(String orderId, ChangeStatusRequest changeStatusRequest) {
        switch (changeStatusRequest.getNewStatus()) {
            case PROCESSING -> changeStatus(orderId, Order.OrderStatus.PROCESSING);
            case SHIPPED -> changeStatus(orderId, Order.OrderStatus.SHIPPED);
            case DELIVERED -> changeStatus(orderId, Order.OrderStatus.DELIVERED);
            case COMPLETED -> changeStatus(orderId, Order.OrderStatus.COMPLETED);
        }
    }

    public UserOrderInfoResponse getUserOrderInfo(String userId) {
        var orders = orderRepository.findByUserIdAndOrderStatusIn(UUID.fromString(userId), Set.of(
                Order.OrderStatus.PENDING_PAYMENT,
                Order.OrderStatus.PAID,
                Order.OrderStatus.PROCESSING,
                Order.OrderStatus.SHIPPED,
                Order.OrderStatus.DELIVERED
        ));

        return UserOrderInfoResponse.fromOrders(orders);
    }

    public UserOrderInfoResponse getFinishedUserOrderInfo(String userId) {
        var orders = orderRepository.findByUserIdAndOrderStatusIn(UUID.fromString(userId), Set.of(Order.OrderStatus.COMPLETED));
        return UserOrderInfoResponse.fromOrders(orders);
    }
}
