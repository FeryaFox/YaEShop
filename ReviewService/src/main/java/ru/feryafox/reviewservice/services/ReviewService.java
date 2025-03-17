package ru.feryafox.reviewservice.services;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.ReviewEvent;
import ru.feryafox.models.internal.responses.UserProfileResponse;
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
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BaseService baseService;
    private final KafkaProducerService kafkaProducerService;
    private final FeignService feignService;

    public CreateReviewResponse createReview(CreateReviewRequest request, String userId) {
        log.info("Создание отзыва на продукт {} пользователем {}", request.getProductId(), userId);

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
        log.info("Отзыв {} успешно создан пользователем {}", review.getId(), userId);

        sendReviewUpdateToKafka(review, ReviewEvent.ReviewStatus.CREATED);
        return new CreateReviewResponse(review.getId());
    }

    public CreateReviewResponse updateReview(String reviewId, UpdateReviewRequest request, String userId) {
        log.info("Обновление отзыва {} пользователем {}", reviewId, userId);

        Optional<Review> optionalReview = reviewRepository.findById(reviewId);

        if (optionalReview.isEmpty()) {
            log.warn("Отзыв {} не найден", reviewId);
            throw new IllegalArgumentException("Отзыв не найден");
        }

        Review review = optionalReview.get();

        if (!review.getAuthor().equals(userId)) {
            log.warn("Пользователь {} попытался изменить чужой отзыв {}", userId, reviewId);
            throw new SecurityException("Вы не можете редактировать чужой отзыв");
        }

        boolean isRatingChanged = request.getRating() != null && !request.getRating().equals(review.getRating());

        if (request.getPositive() != null) review.setPositive(request.getPositive());
        if (request.getNegative() != null) review.setNegative(request.getNegative());
        if (request.getComment() != null) review.setComment(request.getComment());
        if (request.getRating() != null) review.setRating(request.getRating());

        reviewRepository.save(review);
        log.info("Отзыв {} успешно обновлен пользователем {}", reviewId, userId);

        if (isRatingChanged) {
            sendReviewUpdateToKafka(review, ReviewEvent.ReviewStatus.UPDATED);
        }

        return new CreateReviewResponse(review.getId());
    }

    private void sendReviewUpdateToKafka(Review review, ReviewEvent.ReviewStatus status) {
        log.info("Подготовка отправки события ReviewEvent для отзыва {}", review.getId());

        var avgRatingByProductOptional = reviewRepository.getAverageRating(review.getProduct().getId());

        if (avgRatingByProductOptional.isPresent()) {
            var avgRating = avgRatingByProductOptional.get().getAverageRating();
            var countReview = reviewRepository.countByProduct_Id(review.getProduct().getId());
            var event = baseService.convertToReviewEvent(review, avgRating, status, countReview);

            kafkaProducerService.sendReviewUpdate(event);
            log.info("Событие ReviewEvent успешно отправлено в Kafka для отзыва {}", review.getId());
        } else {
            log.warn("Не удалось подсчитать среднюю оценку для продукта {}", review.getProduct().getId());
        }
    }

    public ReviewInfoResponse getReviewInfo(String reviewId) {
        log.info("Запрос информации об отзыве {}", reviewId);

        Review review = baseService.getReview(reviewId);
        UserProfileResponse response;

        try {
            response = feignService.getUserProfile(review.getAuthor());
        } catch (FeignException e) {
            log.error("Ошибка при запросе профиля пользователя {} через Feign: {}", review.getAuthor(), e.getMessage(), e);
            response = null;
        }

        return ReviewInfoResponse.from(review, response);
    }
}
