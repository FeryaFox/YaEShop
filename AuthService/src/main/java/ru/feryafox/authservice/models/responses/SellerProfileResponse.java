package ru.feryafox.authservice.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.feryafox.authservice.entities.Seller;
import ru.feryafox.authservice.entities.User;

@Data
@AllArgsConstructor
@Builder
public class SellerProfileResponse {
    private String id;
    private String phone;
    private String firstName;
    private String surname;
    private String middleName;
    private String shopName;
    private String businessLicense;

    public static SellerProfileResponse from(User user, Seller seller) {
        SellerProfileResponse.SellerProfileResponseBuilder builder = SellerProfileResponse.builder();

        builder.id(String.valueOf(user.getId()));
        builder.phone(user.getPhoneNumber());
        builder.firstName(user.getFirstName());
        builder.surname(user.getSurname());
        builder.middleName(user.getMiddleName());

        builder.shopName(seller.getShopName());
        builder.businessLicense(seller.getBusinessLicense());
        return builder.build();
    }

    public static SellerProfileResponse from(User user) {
        SellerProfileResponse.SellerProfileResponseBuilder builder = SellerProfileResponse.builder();

        builder.id(String.valueOf(user.getId()));
        builder.phone(user.getPhoneNumber());
        builder.firstName(user.getFirstName());
        builder.surname(user.getSurname());
        builder.middleName(user.getMiddleName());

        return builder.build();
    }
}
