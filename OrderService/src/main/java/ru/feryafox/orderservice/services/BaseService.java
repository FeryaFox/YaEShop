package ru.feryafox.orderservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.feryafox.orderservice.entities.Order;
import ru.feryafox.orderservice.exceptions.OrderIsNotExistsException;
import ru.feryafox.orderservice.repsositories.OrderRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BaseService {
    private final OrderRepository orderRepository;

    public Order getOrderById(UUID id) {
        log.info("Поиск заказа по ID: {}", id);

        return orderRepository.findById(id).orElseThrow(() -> {
            log.warn("Заказ с ID {} не найден", id);
            return new OrderIsNotExistsException(id.toString());
        });
    }
}
