package ru.feryafox.shopservice.models.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.feryafox.shopservice.entitis.Shop;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Информация о магазине")
public class ShopInfoResponse {

    @Schema(description = "Идентификатор магазина", example = "123e4567-e89b-12d3-a456-426614174000")
    private String shopId;

    @Schema(description = "Название магазина", example = "Магазин электроники")
    private String name;

    @Schema(description = "Описание магазина", example = "Продажа бытовой техники и электроники")
    private String description;

    @Schema(description = "Ссылка на изображение магазина", example = "https://example.com/shop-image.jpg")
    private String image;

    @Schema(description = "Рейтинг магазина", example = "4.5")
    private double rating;

    public static ShopInfoResponse from(Shop shop) {
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
