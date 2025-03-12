package ru.feryafox.productservice.models.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ при создании продукта")
public class CreateProductResponse {

    @Schema(description = "Идентификатор созданного продукта", example = "60d5f6f7e3a3c9001c8e4bdf")
    private String id;
}
