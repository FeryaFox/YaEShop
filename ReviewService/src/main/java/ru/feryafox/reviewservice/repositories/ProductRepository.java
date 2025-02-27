package ru.feryafox.reviewservice.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.feryafox.reviewservice.entities.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
}
