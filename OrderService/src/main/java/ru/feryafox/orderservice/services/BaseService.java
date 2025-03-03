package ru.feryafox.orderservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feryafox.orderservice.entities.Order;
import ru.feryafox.orderservice.exceptions.OrderIsNotExistsException;
import ru.feryafox.orderservice.repsositories.OrderRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BaseService {
    private final OrderRepository orderRepository;

    public Order getOrderById(UUID id) {
       return orderRepository.findById(id).orElseThrow(() -> new OrderIsNotExistsException(id.toString()));
    }
}
