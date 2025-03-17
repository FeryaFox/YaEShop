package ru.feryafox.shopservice.models.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ при успешном создании магазина")
public class CreateShopResponse {

    @Schema(description = "Идентификатор созданного магазина", example = "123e4567-e89b-12d3-a456-426614174000")
    private String shopId;
}
