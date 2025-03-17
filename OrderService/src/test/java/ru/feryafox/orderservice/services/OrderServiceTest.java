package ru.feryafox.orderservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.feryafox.kafka.models.OrderEvent;
import ru.feryafox.kafka.models.PaymentResponseEvent;
import ru.feryafox.kafka.models.OrderEvent.ProductItem;
import ru.feryafox.orderservice.entities.Order;
import ru.feryafox.orderservice.exceptions.IncorrectStatusChangeException;
import ru.feryafox.orderservice.models.requests.ChangeStatusRequest;
import ru.feryafox.orderservice.models.responses.UserOrderInfoResponse;
import ru.feryafox.orderservice.repsositories.OrderRepository;
import ru.feryafox.orderservice.repsositories.ProductItemRepository;
import ru.feryafox.orderservice.services.kafka.KafkaService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductItemRepository productItemRepository;

    @Mock
    private BaseService baseService;

    @Mock
    private KafkaService kafkaService;

    @InjectMocks
    private OrderService orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = Order.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .orderStatus(Order.OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .totalPrice(BigDecimal.valueOf(100))
                .productItems(Set.of())
                .build();
    }

    @Test
    void testCreateOrder() {
        OrderEvent orderEvent = OrderEvent.builder()
                .orderId(order.getId().toString())
                .userId(order.getUserId().toString())
                .totalPrice(order.getTotalPrice().doubleValue())
                .productItems(Set.of())
                .build();

        when(orderRepository.save(any())).thenReturn(order);
        when(baseService.getOrderById(any())).thenReturn(order);

        orderService.createOrder(orderEvent);

        verify(orderRepository, times(2)).save(any());
    }

    @Test
    void testChangeStatus_ValidTransition() {
        when(baseService.getOrderById(order.getId())).thenReturn(order);
        when(orderRepository.save(any())).thenReturn(order);

        orderService.changeStatus(order.getId().toString(), Order.OrderStatus.PENDING_PAYMENT);

        assertEquals(Order.OrderStatus.PENDING_PAYMENT, order.getOrderStatus());
    }

    @Test
    void testChangeStatus_InvalidTransition() {
        when(baseService.getOrderById(order.getId())).thenReturn(order);

        assertThrows(IncorrectStatusChangeException.class, () ->
                orderService.changeStatus(order.getId().toString(), Order.OrderStatus.SHIPPED));
    }

    @Test
    void testProcessPayment() {
        PaymentResponseEvent paymentResponseEvent = PaymentResponseEvent.builder()
                .orderId(order.getId().toString())
                .build();

        when(baseService.getOrderById(order.getId())).thenReturn(order);
        orderService.changeStatus(String.valueOf(order.getId()), Order.OrderStatus.PENDING_PAYMENT);
        orderService.processPayment(paymentResponseEvent);

        assertEquals(Order.OrderStatus.PAID, order.getOrderStatus());
    }

    @Test
    void testGetUserOrderInfo() {
        order.setProductItems(Set.of());
        when(orderRepository.findByUserIdAndOrderStatusIn(any(), any())).thenReturn(List.of(order));

        UserOrderInfoResponse response = orderService.getUserOrderInfo(order.getUserId().toString());

        assertNotNull(response);
        assertEquals(1, response.getOrders().size());
    }
}
