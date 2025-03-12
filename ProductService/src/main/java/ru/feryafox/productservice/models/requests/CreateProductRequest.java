package ru.feryafox.productservice.models.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на создание продукта")
public class CreateProductRequest {

    @NotBlank(message = "Название продукта обязательно")
    @Size(min = 2, max = 100, message = "Название продукта должно содержать от 2 до 100 символов")
    @Schema(description = "Название продукта", example = "Смартфон Xiaomi Redmi Note 10")
    private String name;

    @NotBlank(message = "Описание продукта обязательно")
    @Size(min = 10, max = 1000, message = "Описание должно содержать от 10 до 1000 символов")
    @Schema(description = "Описание продукта", example = "Мощный смартфон с AMOLED-экраном и батареей 5000 мАч")
    private String description;

    @NotNull(message = "Цена продукта обязательна")
    @DecimalMin(value = "0.01", message = "Цена должна быть больше 0")
    @Schema(description = "Цена продукта", example = "19999.99")
    private double price;

    @NotBlank(message = "Идентификатор магазина обязателен")
    @Schema(description = "Идентификатор магазина, к которому относится продукт", example = "60d5f6f7e3a3c9001c8e4bdf")
    private String shopId;

    @Schema(description = "Дополнительные характеристики продукта", example = "{\"Цвет\":\"Черный\", \"Объем памяти\":\"128GB\"}")
    private Map<String, String> attributes;
}
