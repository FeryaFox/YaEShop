package ru.feryafox.reviewservice.models.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ после создания отзыва")
public class CreateReviewResponse {

    @Schema(description = "Идентификатор созданного отзыва", example = "review123")
    private String id;
}
