package ru.feryafox.authservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.feryafox.authservice.entities.Buyer;

import java.util.UUID;

public interface BuyerRepository extends JpaRepository<Buyer, UUID> {
}