package ru.feryafox.models.internal.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileResponse {
    private String id;
    private String phone;
    private String firstName;
    private String surname;
    private String middleName;
}
