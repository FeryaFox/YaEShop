package ru.feryafox.reviewservice.services;

import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.feryafox.kafka.models.ReviewEvent;
import ru.feryafox.models.internal.responses.UserProfileResponse;
import ru.feryafox.reviewservice.entities.Product;
import ru.feryafox.reviewservice.entities.Review;
import ru.feryafox.reviewservice.exceptions.ReviewAlreadyExistsException;
import ru.feryafox.reviewservice.models.db.AverageRatingResult;
import ru.feryafox.reviewservice.models.requests.CreateReviewRequest;
import ru.feryafox.reviewservice.models.requests.UpdateReviewRequest;
import ru.feryafox.reviewservice.models.responses.CreateReviewResponse;
import ru.feryafox.reviewservice.models.responses.ReviewInfoResponse;
import ru.feryafox.reviewservice.repositories.ReviewRepository;
import ru.feryafox.reviewservice.services.kafka.KafkaProducerService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BaseService baseService;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private FeignService feignService;

    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ReviewEvent mockEvent = new ReviewEvent();
        mockEvent.setReviewId("mockedEventId");
        when(baseService.convertToReviewEvent(any(Review.class), anyDouble(), any(ReviewEvent.ReviewStatus.class), anyLong()))
                .thenReturn(mockEvent);
    }

    @Test
    @DisplayName("createReview: успешно создаёт отзыв при корректных данных")
    void createReview_Success() {
        // given
        String userId = "user123";
        CreateReviewRequest request = new CreateReviewRequest(
                "product123",
                "All good",
                "Long delivery",
                "Overall ok",
                5
        );

        Product mockedProduct = Product.builder()
                .id("product123")
                .shop("shop123")
                .build();

        when(baseService.getProductById("product123")).thenReturn(mockedProduct);
        // isNotReviewExistByUser не найдёт существующего отзыва, значит ошибки не будет
        doNothing().when(baseService).isNotReviewExistByUser("product123", "shop123", userId);

        // Сохраняем отзыв
        Review savedReview = Review.builder()
                .id("review123")
                .author(userId)
                .rating(5)
                .product(mockedProduct)
                .build();
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        // При подготовке события нужно получить средний рейтинг
        AverageRatingResult avgRes = new AverageRatingResult(4.5);
        when(reviewRepository.getAverageRating("product123")).thenReturn(Optional.of(avgRes));
        when(reviewRepository.countByProduct_Id("product123")).thenReturn(10L);

        // when
        CreateReviewResponse response = reviewService.createReview(request, userId);

        // then
        assertNotNull(response);
        assertEquals("review123", response.getId());

        verify(baseService, times(1)).getProductById("product123");
        verify(baseService, times(1)).isNotReviewExistByUser("product123", "shop123", userId);
        verify(reviewRepository, times(1)).save(any(Review.class));
        // проверяем, что пошла отправка события в Kafka
        verify(kafkaProducerService, times(1)).sendReviewUpdate(any(ReviewEvent.class));
    }

    @Test
    @DisplayName("createReview: выбрасывает исключение, если отзыв уже существует у пользователя")
    void createReview_AlreadyExists() {
        // given
        String userId = "user123";
        CreateReviewRequest request = new CreateReviewRequest("product123", null, null, null, 5);

        Product mockedProduct = Product.builder()
                .id("product123")
                .shop("shop123")
                .build();
        when(baseService.getProductById("product123")).thenReturn(mockedProduct);
        // Эмулируем, что у пользователя уже есть отзыв
        doThrow(new ReviewAlreadyExistsException("product123", "shop123", userId))
                .when(baseService).isNotReviewExistByUser("product123", "shop123", userId);

        // when / then
        assertThrows(ReviewAlreadyExistsException.class,
                () -> reviewService.createReview(request, userId));

        verify(reviewRepository, never()).save(any());
        verify(kafkaProducerService, never()).sendReviewUpdate(any());
    }

    @Test
    @DisplayName("updateReview: успешно обновляет отзыв и отправляет событие, если рейтинг поменялся")
    void updateReview_RatingChanged() {
        // given
        String userId = "user123";
        String reviewId = "review123";
        UpdateReviewRequest updateRequest = new UpdateReviewRequest();
        updateRequest.setRating(4);  // меняем рейтинг (был 5)

        Review existingReview = Review.builder()
                .id(reviewId)
                .author(userId)
                .rating(5)
                .product(Product.builder().id("product123").shop("shop123").build())
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(existingReview);

        // для пересчёта среднего рейтинга
        when(reviewRepository.getAverageRating("product123"))
                .thenReturn(Optional.of(new AverageRatingResult(4.0)));
        when(reviewRepository.countByProduct_Id("product123")).thenReturn(11L);

        // when
        CreateReviewResponse response = reviewService.updateReview(reviewId, updateRequest, userId);

        // then
        assertNotNull(response);
        assertEquals(reviewId, response.getId());
        // Проверяем, что событие действительно отправлено (потому что рейтинг изменился)
        verify(kafkaProducerService, times(1)).sendReviewUpdate(any(ReviewEvent.class));
    }

    @Test
    @DisplayName("updateReview: не отправляет событие, если рейтинг не поменялся")
    void updateReview_RatingNotChanged() {
        // given
        String userId = "user123";
        String reviewId = "review123";
        UpdateReviewRequest updateRequest = new UpdateReviewRequest();
        updateRequest.setComment("Just new comment"); // рейтинг не трогаем

        Review existingReview = Review.builder()
                .id(reviewId)
                .author(userId)
                .rating(5)
                .product(Product.builder().id("product123").shop("shop123").build())
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(existingReview);

        // when
        reviewService.updateReview(reviewId, updateRequest, userId);

        // then
        verify(kafkaProducerService, never()).sendReviewUpdate(any(ReviewEvent.class));
    }

    @Test
    @DisplayName("updateReview: бросает ошибку, если отзыв не найден")
    void updateReview_NotFound() {
        // given
        when(reviewRepository.findById("review123")).thenReturn(Optional.empty());

        UpdateReviewRequest request = new UpdateReviewRequest();
        request.setRating(3);

        // when / then
        assertThrows(IllegalArgumentException.class,
                () -> reviewService.updateReview("review123", request, "user123"));
        verify(reviewRepository, never()).save(any());
        verify(kafkaProducerService, never()).sendReviewUpdate(any());
    }

    @Test
    @DisplayName("updateReview: бросает ошибку, если пользователь не автор отзыва")
    void updateReview_Unauthorized() {
        // given
        Review existingReview = Review.builder()
                .id("review123")
                .author("someOtherUser")
                .rating(5)
                .build();
        when(reviewRepository.findById("review123")).thenReturn(Optional.of(existingReview));

        UpdateReviewRequest request = new UpdateReviewRequest();
        request.setRating(4);

        // when / then
        assertThrows(SecurityException.class,
                () -> reviewService.updateReview("review123", request, "anotherUserId"));

        verify(reviewRepository, never()).save(any());
        verify(kafkaProducerService, never()).sendReviewUpdate(any());
    }

    @Test
    @DisplayName("getReviewInfo: возвращает полную информацию об отзыве и профиле пользователя")
    void getReviewInfo_Success() {
        // given
        String reviewId = "review123";
        Review review = Review.builder()
                .id(reviewId)
                .author("user123")
                .positive("Good")
                .negative("Not so good")
                .comment("Some comment")
                .rating(5)
                .build();
        when(baseService.getReview(reviewId)).thenReturn(review);

        UserProfileResponse userProfile = new UserProfileResponse();
        userProfile.setFirstName("Иван");
        userProfile.setSurname("Петров");
        userProfile.setMiddleName("Сергеевич");

        when(feignService.getUserProfile("user123")).thenReturn(userProfile);

        // when
        ReviewInfoResponse info = reviewService.getReviewInfo(reviewId);

        // then
        assertNotNull(info);
        assertEquals("review123", info.getId());
        assertEquals("Иван", info.getFirstName());
        assertEquals("Петров", info.getSurname());
        assertEquals(5, info.getRating());
    }

    @Test
    @DisplayName("getReviewInfo: обрабатывает FeignException и возвращает null-профиль пользователя")
    void getReviewInfo_FeignException() {
        // given
        String reviewId = "review123";
        Review review = Review.builder()
                .id(reviewId)
                .author("user123")
                .build();
        when(baseService.getReview(reviewId)).thenReturn(review);

        // Эмулируем ошибку Feign
        when(feignService.getUserProfile("user123")).thenThrow(FeignException.class);

        // when
        ReviewInfoResponse info = reviewService.getReviewInfo(reviewId);

        // then
        assertNotNull(info);
        assertEquals(reviewId, info.getId());
        // Так как Feign выбросил ошибку, профиль должен быть null, значит поля имени будут null
        assertNull(info.getFirstName());
        assertNull(info.getSurname());
    }
}
