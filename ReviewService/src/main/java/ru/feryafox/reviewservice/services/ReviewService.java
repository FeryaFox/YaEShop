package ru.feryafox.reviewservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.ReviewEvent;
import ru.feryafox.models.internal.responses.UserProfileResponse;
import ru.feryafox.reviewservice.entities.Product;
import ru.feryafox.reviewservice.entities.Review;
import ru.feryafox.reviewservice.models.requests.CreateReviewRequest;
import ru.feryafox.reviewservice.models.requests.UpdateReviewRequest;
import ru.feryafox.reviewservice.models.responses.CreateReviewResponse;
import ru.feryafox.reviewservice.models.responses.ReviewInfoResponse;
import ru.feryafox.reviewservice.repositories.ReviewRepository;
import ru.feryafox.reviewservice.services.kafka.KafkaProducerService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BaseService baseService;
    private final KafkaProducerService kafkaProducerService;
    private final FeignService feignService;

    public CreateReviewResponse createReview(CreateReviewRequest request, String userId) {
        var product = baseService.getProductById(request.getProductId());
        baseService.isNotReviewExistByUser(request.getProductId(), product.getShop(), userId);

        Review review = Review.builder()
                .product(product)
                .positive(request.getPositive())
                .negative(request.getNegative())
                .comment(request.getComment())
                .author(userId)
                .rating(request.getRating())
                .build();

        review = reviewRepository.save(review);
        sendReviewUpdateToKafka(review, ReviewEvent.ReviewStatus.CREATED);
        return new CreateReviewResponse(review.getId());
    }

    public CreateReviewResponse updateReview(String reviewId, UpdateReviewRequest request, String userId) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);

        if (optionalReview.isEmpty()) {
            throw new IllegalArgumentException("Отзыв не найден");
        }

        Review review = optionalReview.get();

        if (!review.getAuthor().equals(userId)) {
            throw new SecurityException("Вы не можете редактировать чужой отзыв");
        }

        boolean isRatingChanged = request.getRating() != null && !request.getRating().equals(review.getRating());

        if (request.getPositive() != null) {
            review.setPositive(request.getPositive());
        }
        if (request.getNegative() != null) {
            review.setNegative(request.getNegative());
        }
        if (request.getComment() != null) {
            review.setComment(request.getComment());
        }
        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }

        reviewRepository.save(review);

        if (isRatingChanged) {
            sendReviewUpdateToKafka(review, ReviewEvent.ReviewStatus.UPDATED);
        }

        return new CreateReviewResponse(review.getId());
    }

    private void sendReviewUpdateToKafka(Review review, ReviewEvent.ReviewStatus status) {
        var avgRatingByProductOptional = reviewRepository.getAverageRating(review.getProduct().getId());

        if (avgRatingByProductOptional.isPresent()) {
            var avgRating = avgRatingByProductOptional.get().getAverageRating();
            var countReview = reviewRepository.countByProduct_Id(review.getProduct().getId());
            var event = baseService.convertToReviewEvent(review, avgRating, status, countReview);

            kafkaProducerService.sendReviewUpdate(event);
        } else {
            System.out.println("Не удалось подсчитать среднюю оценку");
        }
    }

    public ReviewInfoResponse getReviewInfo(String reviewId) {
        Review review = baseService.getReview(reviewId);

        UserProfileResponse response = feignService.getUserProfile(review.getAuthor());

        return ReviewInfoResponse.from(review, response);
    }
}

