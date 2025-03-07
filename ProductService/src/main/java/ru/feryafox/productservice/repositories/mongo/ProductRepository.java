package ru.feryafox.productservice.repositories.mongo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.feryafox.productservice.entities.mongo.Product;
import ru.feryafox.productservice.entities.mongo.Shop;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {

    List<Product> findAllByShop_Id(String shopId, Pageable pageable);
    List<Product> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
}