package ru.feryafox.cartservice.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.feryafox.cartservice.entities.Product;

import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    @Override
    Optional<Product> findById(String s);
}
