package ru.feryafox.authservice.models.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на авторизацию пользователя")
public class LoginRequest {

    @NotBlank(message = "Номер телефона обязателен")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Неверный формат номера телефона")
    @Schema(description = "Номер телефона пользователя", example = "+79991234567")
    private String phoneNumber;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 8, max = 32, message = "Пароль должен содержать от 8 до 32 символов")
    @Schema(description = "Пароль пользователя", example = "P@ssw0rd123")
    private String password;
}
