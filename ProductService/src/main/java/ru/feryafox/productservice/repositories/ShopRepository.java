package ru.feryafox.productservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.feryafox.productservice.entities.Shop;

import java.util.UUID;

public interface ShopRepository extends JpaRepository<Shop, UUID> {
}