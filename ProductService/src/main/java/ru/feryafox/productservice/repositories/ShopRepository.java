package ru.feryafox.productservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.feryafox.productservice.entities.Shop;

import java.util.Optional;
import java.util.UUID;

public interface ShopRepository extends MongoRepository<Shop, String> {
}