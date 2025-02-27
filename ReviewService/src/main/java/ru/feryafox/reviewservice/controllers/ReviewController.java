package ru.feryafox.reviewservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.reviewservice.entities.Review;
import ru.feryafox.reviewservice.models.requests.CreateReviewRequest;
import ru.feryafox.reviewservice.models.requests.UpdateReviewRequest;
import ru.feryafox.reviewservice.services.ReviewService;

@RestController
@RequestMapping("/review/")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("")
    public ResponseEntity<?> createReview(
            @RequestBody @Valid CreateReviewRequest createReviewRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        var response = reviewService.createReview(createReviewRequest, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(
            @PathVariable(value = "reviewId") String reviewId,
            @RequestBody @Valid UpdateReviewRequest updateRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        var response = reviewService.updateReview(reviewId, updateRequest, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}
