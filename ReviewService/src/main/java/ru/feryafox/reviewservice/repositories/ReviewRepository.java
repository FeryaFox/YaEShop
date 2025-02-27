package ru.feryafox.reviewservice.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.feryafox.reviewservice.entities.Review;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
}
