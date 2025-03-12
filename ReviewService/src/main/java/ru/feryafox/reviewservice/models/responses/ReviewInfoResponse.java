package ru.feryafox.reviewservice.models.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.feryafox.models.internal.responses.UserProfileResponse;
import ru.feryafox.reviewservice.entities.Review;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Информация об отзыве")
public class ReviewInfoResponse {

    @Schema(description = "Идентификатор отзыва", example = "review123")
    private String id;

    @Schema(description = "Положительный отзыв", example = "Отличное качество, удобный интерфейс!")
    private String positive;

    @Schema(description = "Отрицательный отзыв", example = "Долгая доставка")
    private String negative;

    @Schema(description = "Дополнительный комментарий", example = "В целом, очень доволен покупкой, рекомендую!")
    private String comment;

    @Schema(description = "Оценка продукта (от 1 до 5)", example = "5")
    private int rating;

    @Schema(description = "Имя автора отзыва", example = "Иван")
    private String firstName;

    @Schema(description = "Фамилия автора отзыва", example = "Петров")
    private String surname;

    @Schema(description = "Отчество автора отзыва", example = "Сергеевич")
    private String middleName;

    public static ReviewInfoResponse from(Review review, UserProfileResponse userProfileResponse) {
        ReviewInfoResponse reviewInfoResponse = new ReviewInfoResponse();
        reviewInfoResponse.setId(review.getId());
        reviewInfoResponse.setPositive(review.getPositive());
        reviewInfoResponse.setNegative(review.getNegative());
        reviewInfoResponse.setComment(review.getComment());
        reviewInfoResponse.setRating(review.getRating());

        if (userProfileResponse != null) {
            reviewInfoResponse.setFirstName(userProfileResponse.getFirstName());
            reviewInfoResponse.setSurname(userProfileResponse.getSurname());
            reviewInfoResponse.setMiddleName(userProfileResponse.getMiddleName());
        }

        return reviewInfoResponse;
    }
}
