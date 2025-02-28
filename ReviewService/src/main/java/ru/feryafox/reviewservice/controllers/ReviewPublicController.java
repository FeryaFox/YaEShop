package ru.feryafox.reviewservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.feryafox.models.internal.responses.UserProfileResponse;
import ru.feryafox.reviewservice.models.responses.ReviewInfoResponse;
import ru.feryafox.reviewservice.services.ReviewService;

@RestController
@RequestMapping("/review/")
@RequiredArgsConstructor
public class ReviewPublicController {
    private final ReviewService reviewService;

    @GetMapping("{reviewId}")
    public ResponseEntity<?> getReview(@PathVariable("reviewId") String reviewId) {
        ReviewInfoResponse response = reviewService.getReviewInfo(reviewId);

        return ResponseEntity.ok(response);
    }
}
