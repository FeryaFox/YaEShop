package ru.feryafox.authservice.models.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.feryafox.authservice.entities.Buyer;
import ru.feryafox.authservice.entities.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Ответ с профилем покупателя")
public class BuyerProfileResponse {

    @Schema(description = "Идентификатор покупателя", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;

    @Schema(description = "Номер телефона покупателя", example = "+79991234567")
    private String phone;

    @Schema(description = "Имя покупателя", example = "Иван")
    private String firstName;

    @Schema(description = "Фамилия покупателя", example = "Петров")
    private String surname;

    @Schema(description = "Отчество покупателя", example = "Сергеевич")
    private String middleName;

    @Schema(description = "Адрес покупателя", example = "ул. Ленина, д. 10, кв. 5")
    private String address;

    @Schema(description = "Дата рождения покупателя", example = "15-06-1990")
    private String dateOfBirth;

    public static BuyerProfileResponse from(User user, Buyer buyer) {
        return BuyerProfileResponse.builder()
                .id(user.getId().toString())
                .phone(user.getPhoneNumber())
                .firstName(user.getFirstName())
                .surname(user.getSurname())
                .middleName(user.getMiddleName())
                .address(buyer.getAddress())
                .dateOfBirth(String.valueOf(buyer.getDateOfBirth()))
                .build();
    }

    public static BuyerProfileResponse from(User user) {
        return BuyerProfileResponse.builder()
                .id(user.getId().toString())
                .phone(user.getPhoneNumber())
                .firstName(user.getFirstName())
                .surname(user.getSurname())
                .middleName(user.getMiddleName())
                .build();
    }
}
