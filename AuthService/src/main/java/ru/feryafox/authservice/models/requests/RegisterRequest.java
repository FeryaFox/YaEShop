package ru.feryafox.authservice.models.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Номер телефона обязателен")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Неверный формат номера телефона")
    private String phoneNumber;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 8, max = 32, message = "Пароль должен содержать от 8 до 32 символов")
    private String password;

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 2, max = 50, message = "Фамилия должна содержать от 2 до 50 символов")
    private String surname;

    @Size(max = 50, message = "Отчество не должно превышать 50 символов")
    private String middleName;

    @NotBlank(message = "Роль пользователя обязательна")
    @Pattern(regexp = "BUYER|SELLER", message = "Роль должна быть либо 'BUYER', либо 'SELLER'")
    private String role;
}
