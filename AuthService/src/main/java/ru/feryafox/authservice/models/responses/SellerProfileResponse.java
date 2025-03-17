package ru.feryafox.authservice.models.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.feryafox.authservice.entities.Seller;
import ru.feryafox.authservice.entities.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Ответ с профилем продавца")
public class SellerProfileResponse {

    @Schema(description = "Идентификатор продавца", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;

    @Schema(description = "Номер телефона продавца", example = "+79991234567")
    private String phone;

    @Schema(description = "Имя продавца", example = "Иван")
    private String firstName;

    @Schema(description = "Фамилия продавца", example = "Петров")
    private String surname;

    @Schema(description = "Отчество продавца", example = "Сергеевич")
    private String middleName;

    @Schema(description = "Название магазина", example = "Лучший магазин")
    private String shopName;

    @Schema(description = "Лицензия на ведение бизнеса", example = "BUS-123456789")
    private String businessLicense;

    public static SellerProfileResponse from(User user, Seller seller) {
        return SellerProfileResponse.builder()
                .id(user.getId().toString())
                .phone(user.getPhoneNumber())
                .firstName(user.getFirstName())
                .surname(user.getSurname())
                .middleName(user.getMiddleName())
                .shopName(seller.getShopName())
                .businessLicense(seller.getBusinessLicense())
                .build();
    }

    public static SellerProfileResponse from(User user) {
        return SellerProfileResponse.builder()
                .id(user.getId().toString())
                .phone(user.getPhoneNumber())
                .firstName(user.getFirstName())
                .surname(user.getSurname())
                .middleName(user.getMiddleName())
                .build();
    }
}
