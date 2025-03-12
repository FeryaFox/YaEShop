package ru.feryafox.productservice.models.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Schema(description = "Запрос на обновление данных продукта")
public class UpdateProductRequest {

    @NotBlank(message = "Название продукта обязательно")
    @Size(min = 2, max = 100, message = "Название продукта должно содержать от 2 до 100 символов")
    @Schema(description = "Название продукта", example = "Смартфон Samsung Galaxy S21")
    private String name;

    @NotBlank(message = "Описание продукта обязательно")
    @Size(min = 10, max = 1000, message = "Описание должно содержать от 10 до 1000 символов")
    @Schema(description = "Описание продукта", example = "Флагманский смартфон с поддержкой 5G и 120Hz экраном")
    private String description;

    @NotNull(message = "Цена продукта обязательна")
    @DecimalMin(value = "0.01", message = "Цена должна быть больше 0")
    @Schema(description = "Цена продукта", example = "79999.99")
    private BigDecimal price;

    @Schema(description = "Дополнительные характеристики продукта", example = "{\"Цвет\":\"Серый\", \"Объем памяти\":\"256GB\"}")
    private Map<String, String> attributes;
}
