package ru.feryafox.reviewservice.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.feryafox.models.internal.responses.UserProfileResponse;
import ru.feryafox.reviewservice.entities.Review;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewInfoResponse {
    private String id;
    private String positive;
    private String negative;
    private String comment;
    private int rating;

    private String firstName;
    private String surname;
    private String middleName;

    public static ReviewInfoResponse from(Review review, UserProfileResponse userProfileResponse) {
        ReviewInfoResponse reviewInfoResponse = new ReviewInfoResponse();
        reviewInfoResponse.setId(review.getId());
        reviewInfoResponse.setPositive(review.getPositive());
        reviewInfoResponse.setNegative(review.getNegative());
        reviewInfoResponse.setComment(review.getComment());
        reviewInfoResponse.setRating(review.getRating());

        reviewInfoResponse.setFirstName(userProfileResponse.getFirstName());
        reviewInfoResponse.setSurname(userProfileResponse.getSurname());
        reviewInfoResponse.setMiddleName(userProfileResponse.getMiddleName());

        return reviewInfoResponse;
    }
}
