package ru.feryafox.reviewservice.models.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на создание отзыва")
public class CreateReviewRequest {

    @NotBlank(message = "Идентификатор продукта обязателен")
    @Schema(description = "Идентификатор продукта, на который оставляется отзыв", example = "product123")
    private String productId;

    @Size(max = 1000, message = "Положительный отзыв не должен превышать 1000 символов")
    @Schema(description = "Положительный отзыв", example = "Отличное качество, удобный интерфейс!")
    private String positive;

    @Size(max = 1000, message = "Отрицательный отзыв не должен превышать 1000 символов")
    @Schema(description = "Отрицательный отзыв", example = "Долгая доставка")
    private String negative;

    @Size(max = 2000, message = "Комментарий не должен превышать 2000 символов")
    @Schema(description = "Дополнительный комментарий", example = "В целом, очень доволен покупкой, рекомендую!")
    private String comment;

    @NotNull(message = "Рейтинг должен быть указан")
    @Min(value = 1, message = "Рейтинг должен быть не менее 1")
    @Max(value = 5, message = "Рейтинг должен быть не более 5")
    @Schema(description = "Оценка продукта (от 1 до 5)", example = "5")
    private Integer rating;
}
