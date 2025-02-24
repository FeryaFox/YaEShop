package ru.feryafox.productservice.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.feryafox.productservice.entities.mongo.Shop;

public interface ShopRepository extends MongoRepository<Shop, String> {
}