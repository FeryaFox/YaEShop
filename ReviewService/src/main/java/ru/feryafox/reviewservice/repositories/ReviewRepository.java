package ru.feryafox.reviewservice.repositories;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.feryafox.reviewservice.entities.Review;
import ru.feryafox.reviewservice.models.db.AverageRatingResult;

import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    Optional<Review> findByProduct_IdAndAuthor(String id, String author);

    @Aggregation(pipeline = {
            "{ '$match': { 'productId': ?0 } }",
            "{ '$group': { '_id': null, 'averageRating': { '$avg': '$rating' } } }"
    })
    Optional<AverageRatingResult> getAverageRating(String productId);
}
