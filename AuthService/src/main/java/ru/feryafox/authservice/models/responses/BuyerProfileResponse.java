package ru.feryafox.authservice.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class BuyerProfileResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String address;
    private Date dateOfBirth;
}
