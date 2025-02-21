package ru.feryafox.authservice.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.feryafox.authservice.entities.Buyer;
import ru.feryafox.authservice.entities.User;

import java.util.Date;

@Data
@AllArgsConstructor
@Builder
public class BuyerProfileResponse {
    private String id;
    private String phone;
    private String firstName;
    private String surname;
    private String middleName;
    private String address;
    private String dateOfBirth;

    public static BuyerProfileResponse from(User user, Buyer buyer) {

        BuyerProfileResponse.BuyerProfileResponseBuilder buyerProfileResponseBuilder = BuyerProfileResponse.builder();

        buyerProfileResponseBuilder.id(user.getId().toString());
        buyerProfileResponseBuilder.phone(user.getPhoneNumber());
        buyerProfileResponseBuilder.firstName(user.getFirstName());
        buyerProfileResponseBuilder.surname(user.getSurname());
        buyerProfileResponseBuilder.middleName(user.getMiddleName());

        buyerProfileResponseBuilder.address(buyer.getAddress());
        buyerProfileResponseBuilder.dateOfBirth(String.valueOf(buyer.getDateOfBirth()));

        return buyerProfileResponseBuilder.build();
    }


    public static BuyerProfileResponse from(User user) {

        BuyerProfileResponse.BuyerProfileResponseBuilder buyerProfileResponseBuilder = BuyerProfileResponse.builder();

        buyerProfileResponseBuilder.id(user.getId().toString());
        buyerProfileResponseBuilder.phone(user.getPhoneNumber());
        buyerProfileResponseBuilder.firstName(user.getFirstName());
        buyerProfileResponseBuilder.surname(user.getSurname());
        buyerProfileResponseBuilder.middleName(user.getMiddleName());

        return buyerProfileResponseBuilder.build();
    }
}
