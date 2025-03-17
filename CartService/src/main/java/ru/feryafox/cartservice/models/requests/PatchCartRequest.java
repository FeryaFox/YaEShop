package ru.feryafox.cartservice.models.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Запрос на обновление количества товара в корзине")
public class PatchCartRequest {

    @Min(value = 1, message = "Количество товара должно быть не менее 1")
    @Schema(description = "Новое количество товара", example = "5")
    private int quantity;
}
