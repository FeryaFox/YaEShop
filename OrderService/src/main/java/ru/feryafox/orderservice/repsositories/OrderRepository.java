package ru.feryafox.orderservice.repsositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.feryafox.orderservice.entities.Order;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserIdAndOrderStatusIn(UUID userId, Set<Order.OrderStatus> statuses);
}