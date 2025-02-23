package ru.feryafox.productservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.feryafox.productservice.entities.Image;

import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {
}