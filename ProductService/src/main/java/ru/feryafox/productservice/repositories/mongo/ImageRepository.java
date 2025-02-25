package ru.feryafox.productservice.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.feryafox.productservice.entities.mongo.Image;

public interface ImageRepository extends MongoRepository<Image, String> {

}