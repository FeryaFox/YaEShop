package ru.feryafox.authservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.feryafox.authservice.entities.Seller;

import java.util.UUID;

public interface SellerRepository extends JpaRepository<Seller, UUID> {
}