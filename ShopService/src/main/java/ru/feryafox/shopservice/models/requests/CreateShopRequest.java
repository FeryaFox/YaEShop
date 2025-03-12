package ru.feryafox.shopservice.models.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Запрос на создание магазина")
public class CreateShopRequest {

    @NotBlank(message = "Название магазина обязательно")
    @Size(min = 2, max = 100, message = "Название магазина должно содержать от 2 до 100 символов")
    @Schema(description = "Название магазина", example = "Магазин электроники")
    private String name;

    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    @Schema(description = "Описание магазина", example = "Продажа бытовой техники и электроники")
    private String description;
}
