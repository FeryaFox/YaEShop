package ru.feryafox.productservice.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.feryafox.productservice.entities.mongo.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
}