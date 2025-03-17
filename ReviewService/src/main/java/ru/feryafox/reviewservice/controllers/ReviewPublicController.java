package ru.feryafox.reviewservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.reviewservice.models.responses.ReviewInfoResponse;
import ru.feryafox.reviewservice.services.ReviewService;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
@Tag(name = "ReviewPublicController", description = "Публичные методы работы с отзывами")
public class ReviewPublicController {
    private final ReviewService reviewService;

    @Operation(summary = "Получить информацию об отзыве", description = "Возвращает данные конкретного отзыва по его ID.")
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewInfoResponse> getReview(
            @PathVariable @Parameter(description = "Идентификатор отзыва", required = true) String reviewId
    ) {
        return ResponseEntity.ok(reviewService.getReviewInfo(reviewId));
    }
}
