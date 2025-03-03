package ru.feryafox.orderservice.repsositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.feryafox.orderservice.entities.ProductItem;

import java.util.UUID;

public interface ProductItemRepository extends JpaRepository<ProductItem, UUID> {
}