package ru.feryafox.reviewservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.feryafox.models.internal.responses.UserProfileResponse;

@RestController
@RequestMapping("/review/")
@RequiredArgsConstructor
public class ReviewPublicController {
    @GetMapping("{reviewId}")
    public ResponseEntity<?> getReview(@PathVariable long reviewId) {
        UserProfileResponse response =
    }
}
