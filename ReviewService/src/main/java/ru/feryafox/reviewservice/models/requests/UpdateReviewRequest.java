package ru.feryafox.reviewservice.models.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateReviewRequest {
    @Size(max = 1000, message = "Положительный отзыв не должен превышать 1000 символов")
    private String positive;

    @Size(max = 1000, message = "Отрицательный отзыв не должен превышать 1000 символов")
    private String negative;

    @Size(max = 2000, message = "Комментарий не должен превышать 2000 символов")
    private String comment;

    @Min(value = 1, message = "Рейтинг должен быть не менее 1")
    @Max(value = 5, message = "Рейтинг должен быть не более 5")
    private Integer rating;
}
