package ru.feryafox.reviewservice.models.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на обновление отзыва")
public class UpdateReviewRequest {

    @Size(max = 1000, message = "Положительный отзыв не должен превышать 1000 символов")
    @Schema(description = "Обновленный положительный отзыв", example = "Прекрасный сервис и удобный функционал!")
    private String positive;

    @Size(max = 1000, message = "Отрицательный отзыв не должен превышать 1000 символов")
    @Schema(description = "Обновленный отрицательный отзыв", example = "Хотелось бы быстрее получать товар")
    private String negative;

    @Size(max = 2000, message = "Комментарий не должен превышать 2000 символов")
    @Schema(description = "Обновленный комментарий", example = "Использую уже месяц, всё нравится, но доставка могла бы быть быстрее")
    private String comment;

    @Min(value = 1, message = "Рейтинг должен быть не менее 1")
    @Max(value = 5, message = "Рейтинг должен быть не более 5")
    @Schema(description = "Обновленная оценка продукта (от 1 до 5)", example = "4")
    private Integer rating;
}
