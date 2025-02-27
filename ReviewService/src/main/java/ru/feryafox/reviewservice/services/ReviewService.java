package ru.feryafox.reviewservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.ReviewEvent;
import ru.feryafox.reviewservice.entities.Product;
import ru.feryafox.reviewservice.entities.Review;
import ru.feryafox.reviewservice.models.requests.CreateReviewRequest;
import ru.feryafox.reviewservice.models.responses.CreateReviewResponse;
import ru.feryafox.reviewservice.repositories.ReviewRepository;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BaseService baseService;

    public CreateReviewResponse createReview(CreateReviewRequest request, String userId) {

        Product product = baseService.getProductById(request.getProductId());

        baseService.isNotReviewExistByUser(request.getProductId(), product.getShop(), userId);


        Review review = new Review();

        review.setProduct(product);
        review.setPositive(request.getPositive());
        review.setNegative(request.getNegative());
        review.setComment(request.getComment());
        review.setAuthor(userId);
        review.setRating(request.getRating());

        review = reviewRepository.save(review);

        var avgRatingByProductOptional = reviewRepository.getAverageRating(review.getProduct().getId());

        if (avgRatingByProductOptional.isPresent()) {
            var avgRating = avgRatingByProductOptional.get().getAverageRating();
            var event = baseService.convertToReviewEvent(review, avgRating, ReviewEvent.ReviewStatus.CREATED);

            // STOP HERE
        }
    }
}
