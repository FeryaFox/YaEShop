package ru.feryafox.productservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.feryafox.productservice.entities.Product;

import java.util.UUID;

public interface ProductRepository extends MongoRepository<Product, String> {
}