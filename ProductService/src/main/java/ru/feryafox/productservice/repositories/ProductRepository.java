package ru.feryafox.productservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.feryafox.productservice.entities.Product;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}