package ru.feryafox.reviewservice.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReviewRequest {
    private String productId;
    private String positive;
    private String negative;
    private String comment;
    private int rating;
}
