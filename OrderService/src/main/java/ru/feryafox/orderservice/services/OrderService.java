
package ru.feryafox.orderservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductItemRepository productItemRepository;
    private final BaseService baseService;
    private final KafkaService kafkaService;

    @Transactional
    public void createOrder(OrderEvent orderEvent) {
        log.info("Создание нового заказа: {}", orderEvent.getOrderId());

        UUID orderId;
        UUID userId;
        try {
            orderId = UUID.fromString(orderEvent.getOrderId());
            userId = UUID.fromString(orderEvent.getUserId());
        } catch (IllegalArgumentException e) {
            log.error("Ошибка: Некорректный UUID в событии OrderEvent: {}", orderEvent, e);
            return;
        }

        Order order = Order.builder()
                .id(orderId)
                .userId(userId)
                .orderStatus(Order.OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .totalPrice(BigDecimal.valueOf(orderEvent.getTotalPrice()))
                .build();

        Set<ProductItem> productItems = ProductItem.createProductsFromEvent(orderEvent.getProductItems(), order);
        order.setProductItems(productItems);

        orderRepository.save(order);
        log.info("Заказ {} успешно создан для пользователя {}", orderId, userId);

        changeStatus(order.getId().toString(), Order.OrderStatus.PENDING_PAYMENT);
    }

    @Transactional
    public void changeStatus(String orderId, Order.OrderStatus newStatus) {
        log.info("Изменение статуса заказа {} на {}", orderId, newStatus);

        UUID orderUuid;
        try {
            orderUuid = UUID.fromString(orderId);
        } catch (IllegalArgumentException e) {
            log.error("Ошибка: Некорректный формат UUID для orderId: {}", orderId, e);
            return;
        }

        var order = baseService.getOrderById(orderUuid);

        try {
            switch (newStatus) {
                case PENDING_PAYMENT -> {
                    if (!order.getOrderStatus().equals(Order.OrderStatus.CREATED)) throw new IncorrectStatusChangeException(order.getOrderStatus(), newStatus);
                    order.setOrderStatus(Order.OrderStatus.PENDING_PAYMENT);
                    kafkaService.createPaymentRequest(order);
                }
                case PAID -> {
                    if (!order.getOrderStatus().equals(Order.OrderStatus.PENDING_PAYMENT)) throw new IncorrectStatusChangeException(order.getOrderStatus(), newStatus);
                    order.setOrderStatus(Order.OrderStatus.PAID);
                }
                case PROCESSING -> {
                    if (!order.getOrderStatus().equals(Order.OrderStatus.PAID)) throw new IncorrectStatusChangeException(order.getOrderStatus(), newStatus);
                    order.setOrderStatus(Order.OrderStatus.PROCESSING);
                }
                case SHIPPED -> {
                    if (!order.getOrderStatus().equals(Order.OrderStatus.PROCESSING)) throw new IncorrectStatusChangeException(order.getOrderStatus(), newStatus);
                    order.setOrderStatus(Order.OrderStatus.SHIPPED);
                }
                case DELIVERED -> {
                    if (!order.getOrderStatus().equals(Order.OrderStatus.SHIPPED)) throw new IncorrectStatusChangeException(order.getOrderStatus(), newStatus);
                    order.setOrderStatus(Order.OrderStatus.DELIVERED);
                }
                case COMPLETED -> {
                    if (!order.getOrderStatus().equals(Order.OrderStatus.DELIVERED)) throw new IncorrectStatusChangeException(order.getOrderStatus(), newStatus);
                    order.setOrderStatus(Order.OrderStatus.COMPLETED);
                }
            }

            orderRepository.save(order);
            log.info("Статус заказа {} успешно обновлен на {}", orderId, newStatus);
        } catch (IncorrectStatusChangeException e) {
            log.error("Ошибка при изменении статуса заказа {}: {}", orderId, e.getMessage());
        }
    }

    @Transactional
    public void processPayment(PaymentResponseEvent paymentResponseEvent) {
        log.info("Обработка платежа для заказа {}", paymentResponseEvent.getOrderId());
        changeStatus(paymentResponseEvent.getOrderId(), Order.OrderStatus.PAID);
    }

    @Transactional
    public void changeOrderStatus(String orderId, ChangeStatusRequest changeStatusRequest) {
        log.info("Изменение статуса заказа {} на {}", orderId, changeStatusRequest.getNewStatus());
        switch (changeStatusRequest.getNewStatus()) {
            case PROCESSING -> changeStatus(orderId, Order.OrderStatus.PROCESSING);
            case SHIPPED -> changeStatus(orderId, Order.OrderStatus.SHIPPED);
            case DELIVERED -> changeStatus(orderId, Order.OrderStatus.DELIVERED);
            case COMPLETED -> changeStatus(orderId, Order.OrderStatus.COMPLETED);
        }
    }

    public UserOrderInfoResponse getUserOrderInfo(String userId) {
        log.info("Получение информации о заказах пользователя {}", userId);

        UUID userUuid;
        try {
            userUuid = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            log.error("Ошибка: Некорректный UUID пользователя: {}", userId, e);
            return null;
        }

        var orders = orderRepository.findByUserIdAndOrderStatusIn(userUuid, Set.of(
                Order.OrderStatus.PENDING_PAYMENT,
                Order.OrderStatus.PAID,
                Order.OrderStatus.PROCESSING,
                Order.OrderStatus.SHIPPED,
                Order.OrderStatus.DELIVERED
        ));

        return UserOrderInfoResponse.fromOrders(orders);
    }

    public UserOrderInfoResponse getFinishedUserOrderInfo(String userId) {
        log.info("Получение завершенных заказов пользователя {}", userId);

        UUID userUuid;
        try {
            userUuid = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            log.error("Ошибка: Некорректный UUID пользователя: {}", userId, e);
            return null;
        }

        var orders = orderRepository.findByUserIdAndOrderStatusIn(userUuid, Set.of(Order.OrderStatus.COMPLETED));
        return UserOrderInfoResponse.fromOrders(orders);
    }
}
