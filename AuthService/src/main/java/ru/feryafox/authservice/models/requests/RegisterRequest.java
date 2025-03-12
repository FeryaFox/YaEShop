package ru.feryafox.authservice.models.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на регистрацию пользователя")
public class RegisterRequest {

    @NotBlank(message = "Номер телефона обязателен")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Неверный формат номера телефона")
    @Schema(description = "Номер телефона пользователя", example = "+79991234567")
    private String phoneNumber;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 8, max = 32, message = "Пароль должен содержать от 8 до 32 символов")
    @Schema(description = "Пароль пользователя", example = "P@ssw0rd123")
    private String password;

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов")
    @Schema(description = "Имя пользователя", example = "Иван")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 2, max = 50, message = "Фамилия должна содержать от 2 до 50 символов")
    @Schema(description = "Фамилия пользователя", example = "Петров")
    private String surname;

    @Size(max = 50, message = "Отчество не должно превышать 50 символов")
    @Schema(description = "Отчество пользователя", example = "Сергеевич")
    private String middleName;

    @NotBlank(message = "Роль пользователя обязательна")
    @Pattern(regexp = "BUYER|SELLER", message = "Роль должна быть либо 'BUYER', либо 'SELLER'")
    @Schema(description = "Роль пользователя", example = "BUYER")
    private String role;
}
