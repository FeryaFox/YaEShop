package ru.feryafox.models.internal.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopInfoInternalResponse {
    private String shopId;
    private String userId;
    private String name;
    private String description;
    private String image;
}
