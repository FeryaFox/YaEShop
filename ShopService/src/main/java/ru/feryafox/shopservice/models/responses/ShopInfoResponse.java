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
    private double rating;

    public static ShopInfoResponse from (Shop shop) {
        ShopInfoResponse.ShopInfoResponseBuilder builder = ShopInfoResponse.builder();

        var rating = shop.getRating();

        builder.shopId(String.valueOf(shop.getId()))
                .name(shop.getShopName())
                .description(shop.getShopDescription())
                .image(shop.getShopImage())
                .rating(rating != null ? rating.doubleValue() : 0.0);

        return builder.build();
    }
}
