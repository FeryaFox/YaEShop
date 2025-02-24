package ru.feryafox.productservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.feryafox.productservice.entities.Image;

import java.util.UUID;

public interface ImageRepository extends MongoRepository<Image, String> {
}