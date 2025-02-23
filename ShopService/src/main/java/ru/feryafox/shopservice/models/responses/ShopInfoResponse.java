package ru.feryafox.shopservice.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.feryafox.shopservice.entitis.Shop;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopInfoResponse {
    private String shopId;
    private String name;
    private String description;
    private String image;

    public static ShopInfoResponse from (Shop shop) {
        ShopInfoResponse.ShopInfoResponseBuilder builder = ShopInfoResponse.builder();

        builder.shopId(String.valueOf(shop.getId()));
        builder.name(shop.getShopName());
        builder.description(shop.getShopDescription());
        builder.image(shop.getShopImage());

        return builder.build();
    }
}
