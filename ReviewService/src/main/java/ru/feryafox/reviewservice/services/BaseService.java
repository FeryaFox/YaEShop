package ru.feryafox.reviewservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.ReviewEvent;
import ru.feryafox.reviewservice.entities.Product;
import ru.feryafox.reviewservice.entities.Review;
import ru.feryafox.reviewservice.exceptions.ProductIsNotExistsException;
import ru.feryafox.reviewservice.exceptions.ReviewAlreadyExistsException;
import ru.feryafox.reviewservice.exceptions.ReviewIsNotExistsException;
import ru.feryafox.reviewservice.repositories.ProductRepository;
import ru.feryafox.reviewservice.repositories.ReviewRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class BaseService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public void isNotReviewExistByUser(String productId, String shopId, String userId) {
        log.info("Проверка существования отзыва пользователя {} на продукт {} в магазине {}", userId, productId, shopId);
        var review = reviewRepository.findByProduct_IdAndAuthor(productId, userId);
        if (review.isPresent()) {
            log.warn("Отзыв уже существует. Пользователь: {}, Продукт: {}, Магазин: {}", userId, productId, shopId);
            throw new ReviewAlreadyExistsException(productId, shopId, userId);
        }
        log.info("Отзыв не найден. Пользователь {} может оставить отзыв на продукт {}", userId, productId);
    }

    public Product getProductById(String productId) {
        log.info("Поиск продукта по ID: {}", productId);
        return productRepository.findById(productId).orElseThrow(() -> {
            log.warn("Продукт с ID {} не найден", productId);
            return new ProductIsNotExistsException(productId);
        });
    }

    public ReviewEvent convertToReviewEvent(Review review, double avgProductRating, ReviewEvent.ReviewStatus reviewStatus, long countProductReviews) {
        log.info("Конвертация отзыва {} в событие ReviewEvent", review.getId());

        ReviewEvent event = new ReviewEvent();
        event.setReviewId(review.getId());
        event.setShopId(review.getProduct().getShop());
        event.setProductId(review.getProduct().getId());
        event.setProductName(review.getProduct().getName());
        event.setAuthor(review.getAuthor());
        event.setRating(review.getRating());
        event.setAvgProductRating(avgProductRating);
        event.setStatus(reviewStatus);
        event.setCountProductReviews(countProductReviews);

        log.info("Событие ReviewEvent успешно создано: {}", event);
        return event;
    }

    public Review getReview(String reviewId) {
        log.info("Поиск отзыва по ID: {}", reviewId);
        return reviewRepository.findById(reviewId).orElseThrow(() -> {
            log.warn("Отзыв с ID {} не найден", reviewId);
            return new ReviewIsNotExistsException(reviewId);
        });
    }
}
