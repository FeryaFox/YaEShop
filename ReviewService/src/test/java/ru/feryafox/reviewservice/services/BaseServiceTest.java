package ru.feryafox.reviewservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.feryafox.kafka.models.ReviewEvent;
import ru.feryafox.reviewservice.entities.Product;
import ru.feryafox.reviewservice.entities.Review;
import ru.feryafox.reviewservice.exceptions.ProductIsNotExistsException;
import ru.feryafox.reviewservice.exceptions.ReviewAlreadyExistsException;
import ru.feryafox.reviewservice.exceptions.ReviewIsNotExistsException;
import ru.feryafox.reviewservice.repositories.ProductRepository;
import ru.feryafox.reviewservice.repositories.ReviewRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BaseServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private BaseService baseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("isNotReviewExistByUser: выбрасывает ReviewAlreadyExistsException, если отзыв найден")
    void isNotReviewExistByUser_ThrowExceptionIfReviewExists() {
        // given
        String productId = "product123";
        String shopId = "shop123";
        String userId = "user123";

        Review existingReview = new Review();
        when(reviewRepository.findByProduct_IdAndAuthor(productId, userId))
                .thenReturn(Optional.of(existingReview));

        // when / then
        assertThrows(ReviewAlreadyExistsException.class, () ->
                baseService.isNotReviewExistByUser(productId, shopId, userId));
    }

    @Test
    @DisplayName("isNotReviewExistByUser: не бросает исключений, если отзыв не найден")
    void isNotReviewExistByUser_NoExceptionIfReviewNotExists() {
        // given
        String productId = "product123";
        String shopId = "shop123";
        String userId = "user123";

        when(reviewRepository.findByProduct_IdAndAuthor(productId, userId))
                .thenReturn(Optional.empty());

        // should not throw
        assertDoesNotThrow(() ->
                baseService.isNotReviewExistByUser(productId, shopId, userId));
    }

    @Test
    @DisplayName("getProductById: бросает ProductIsNotExistsException, если продукт не найден")
    void getProductById_ThrowIfNotFound() {
        when(productRepository.findById("product123")).thenReturn(Optional.empty());
        assertThrows(ProductIsNotExistsException.class, () ->
                baseService.getProductById("product123"));
    }

    @Test
    @DisplayName("getProductById: возвращает продукт, если он найден")
    void getProductById_Success() {
        Product product = new Product();
        product.setId("product123");

        when(productRepository.findById("product123")).thenReturn(Optional.of(product));

        Product result = baseService.getProductById("product123");
        assertNotNull(result);
        assertEquals("product123", result.getId());
    }

    @Test
    @DisplayName("convertToReviewEvent: проверяем корректное маппирование Review -> ReviewEvent")
    void convertToReviewEvent_SuccessMapping() {
        Product product = Product.builder()
                .id("product123")
                .name("Test product")
                .shop("shop123")
                .build();

        Review review = Review.builder()
                .id("review123")
                .author("user123")
                .rating(5)
                .product(product)
                .build();

        double avgRating = 4.5;
        long countReviews = 10;

        ReviewEvent event = baseService.convertToReviewEvent(review, avgRating,
                ReviewEvent.ReviewStatus.CREATED, countReviews);

        assertNotNull(event);
        assertEquals("review123", event.getReviewId());
        assertEquals("product123", event.getProductId());
        assertEquals(5, event.getRating());
        assertEquals(4.5, event.getAvgProductRating());
        assertEquals(ReviewEvent.ReviewStatus.CREATED, event.getStatus());
        assertEquals(countReviews, event.getCountProductReviews());
    }

    @Test
    @DisplayName("getReview: бросает ReviewIsNotExistsException, если отзыв не найден")
    void getReview_ThrowIfNotFound() {
        when(reviewRepository.findById("review123")).thenReturn(Optional.empty());
        assertThrows(ReviewIsNotExistsException.class, () ->
                baseService.getReview("review123"));
    }

    @Test
    @DisplayName("getReview: возвращает отзыв, если он найден")
    void getReview_Success() {
        Review review = new Review();
        review.setId("review123");

        when(reviewRepository.findById("review123")).thenReturn(Optional.of(review));

        Review result = baseService.getReview("review123");
        assertNotNull(result);
        assertEquals("review123", result.getId());
    }
}
