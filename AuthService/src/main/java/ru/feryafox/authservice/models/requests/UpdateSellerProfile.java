package ru.feryafox.authservice.models.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на обновление профиля продавца")
public class UpdateSellerProfile {

    @NotBlank(message = "Название магазина обязательно")
    @Size(min = 2, max = 100, message = "Название магазина должно содержать от 2 до 100 символов")
    @Schema(description = "Название магазина", example = "Лучший магазин")
    private String shopName;

    @NotBlank(message = "Лицензия на бизнес обязательна")
    @Size(min = 5, max = 50, message = "Номер лицензии должен содержать от 5 до 50 символов")
    @Schema(description = "Лицензия на ведение бизнеса", example = "BUS-123456789")
    private String businessLicense;
}
