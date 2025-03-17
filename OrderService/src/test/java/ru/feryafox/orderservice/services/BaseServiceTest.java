package ru.feryafox.orderservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.feryafox.orderservice.entities.Order;
import ru.feryafox.orderservice.exceptions.OrderIsNotExistsException;
import ru.feryafox.orderservice.repsositories.OrderRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private BaseService baseService;

    private Order order;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        order = Order.builder()
                .id(orderId)
                .userId(UUID.randomUUID())
                .orderStatus(Order.OrderStatus.CREATED)
                .build();
    }

    @Test
    void testGetOrderById_OrderExists() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order result = baseService.getOrderById(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.getId());
    }

    @Test
    void testGetOrderById_OrderDoesNotExist() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderIsNotExistsException.class, () -> baseService.getOrderById(orderId));
    }
}
