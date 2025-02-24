package ru.feryafox.productservice.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.feryafox.productservice.entities.mongo.Image;

public interface ImageRepository extends MongoRepository<Image, String> {
}