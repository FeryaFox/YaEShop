package ru.feryafox.reviewservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.reviewservice.models.requests.CreateReviewRequest;
import ru.feryafox.reviewservice.models.requests.UpdateReviewRequest;
import ru.feryafox.reviewservice.services.ReviewService;

@RestController
@RequestMapping("/review/")
@RequiredArgsConstructor
@Tag(name = "ReviewController", description = "Управление отзывами пользователей")
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(summary = "Создать отзыв", description = "Позволяет пользователю оставить отзыв на продукт.")
    @PostMapping("")
    public ResponseEntity<?> createReview(
            @RequestBody @Valid CreateReviewRequest createReviewRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        var response = reviewService.createReview(createReviewRequest, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Обновить отзыв", description = "Позволяет пользователю обновить ранее оставленный отзыв.")
    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(
            @PathVariable(value = "reviewId")
            @Parameter(description = "Идентификатор отзыва", required = true)
            String reviewId,
            @RequestBody @Valid UpdateReviewRequest updateRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        var response = reviewService.updateReview(reviewId, updateRequest, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}
