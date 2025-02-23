package ru.feryafox.shopservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.feryafox.shopservice.entitis.Shop;

import java.util.Optional;
import java.util.UUID;

public interface ShopRepository extends JpaRepository<Shop, UUID> {
    @Override
    Optional<Shop> findById(UUID uuid);
}