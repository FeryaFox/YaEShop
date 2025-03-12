package ru.feryafox.shopservice.models.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на обновление данных магазина")
public class UpdateShopRequest {

    @Size(min = 2, max = 100, message = "Название магазина должно содержать от 2 до 100 символов")
    @Schema(description = "Новое название магазина", example = "Обновленный магазин электроники")
    private String name;

    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    @Schema(description = "Новое описание магазина", example = "Теперь у нас больше скидок и акций")
    private String description;
}
