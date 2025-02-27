package ru.feryafox.reviewservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.ReviewEvent;
import ru.feryafox.reviewservice.entities.Product;
import ru.feryafox.reviewservice.entities.Review;
import ru.feryafox.reviewservice.exceptions.ProductIsNotExistsException;
import ru.feryafox.reviewservice.exceptions.ReviewAlreadyExistsException;
import ru.feryafox.reviewservice.repositories.ProductRepository;
import ru.feryafox.reviewservice.repositories.ReviewRepository;

@Service
@RequiredArgsConstructor
public class BaseService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public void isNotReviewExistByUser(String productId, String shopId, String userId) {
        var review = reviewRepository.findByProduct_IdAndAuthor(productId, userId);
        if (review.isPresent()) throw new ReviewAlreadyExistsException(productId, shopId, userId);
    }

    public Product getProductById(String productId) {
        return productRepository.findById(productId).orElseThrow(() -> new ProductIsNotExistsException(productId));
    }

    public ReviewEvent convertToReviewEvent(Review review, double avgProductRating, ReviewEvent.ReviewStatus reviewStatus) {
        ReviewEvent event = new ReviewEvent();
        event.setReviewId(review.getId());
        event.setShopId(review.getProduct().getShop());
        event.setProductId(review.getProduct().getId());
        event.setProductName(review.getProduct().getName());
        event.setAuthor(review.getAuthor());
        event.setRating(review.getRating());
        event.setAvgProductRating(avgProductRating);
        event.setStatus(reviewStatus);

        return event;
    }
}
