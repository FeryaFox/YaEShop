package ru.feryafox.reviewservice.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewInfoResponse {
    private String id;
    private String positive;
    private String negative;
    private String comment;

    private String firstName;
    private String surname;
    private String middleName;
}
