package ru.feryafox.productservice.models.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.feryafox.models.internal.responses.ProductInfoInternResponse;
import ru.feryafox.productservice.entities.mongo.Image;
import ru.feryafox.productservice.entities.mongo.Product;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Информация о продукте")
public class ProductInfoResponse {

    @Schema(description = "Идентификатор продукта", example = "60d5f6f7e3a3c9001c8e4bdf")
    private String id;

    @Schema(description = "Название продукта", example = "Смартфон Apple iPhone 13")
    private String name;

    @Schema(description = "Описание продукта", example = "Мощный смартфон с камерой 12 МП и процессором A15 Bionic")
    private String description;

    @Schema(description = "Идентификатор магазина, к которому относится продукт", example = "60d5f6f7e3a3c9001c8e4bdc")
    private String shopId;

    @Schema(description = "Список изображений продукта")
    private Set<ImageResponse> images;

    @Schema(description = "Дополнительные характеристики продукта", example = "{\"Цвет\":\"Синий\", \"Объем памяти\":\"128GB\"}")
    private Map<String, String> attributes;

    @Schema(description = "Цена продукта", example = "89999.99")
    private double price;

    @Schema(description = "Рейтинг продукта", example = "4.7")
    private double rating;

    public static ProductInfoResponse from(Product product) {
        ProductInfoResponse productInfoResponse = new ProductInfoResponse();
        productInfoResponse.setId(product.getId());
        productInfoResponse.setName(product.getName());
        productInfoResponse.setDescription(product.getDescription());
        productInfoResponse.setShopId(product.getShop().getId());
        productInfoResponse.setAttributes(product.getAttributes());
        productInfoResponse.setPrice(product.getPrice().doubleValue());
        productInfoResponse.setRating(product.getProductRating());

        productInfoResponse.setImages(product.getImages().stream()
                .map(ImageResponse::fromImage)
                .sorted(Comparator.comparingInt(ImageResponse::getPosition))
                .collect(Collectors.toCollection(LinkedHashSet::new))
        );

        return productInfoResponse;
    }

    public static ProductInfoInternResponse toIntern(ProductInfoResponse product) {
        var productInfoInternResponseBuilder = ProductInfoInternResponse.builder();

        productInfoInternResponseBuilder
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .shopId(product.getShopId())
                .images(product.getImages().stream().map(ImageResponse::toIntern).collect(Collectors.toCollection(LinkedHashSet::new)))
                .attributes(product.getAttributes())
                .price(product.getPrice())
                .rating(product.getRating());

        return productInfoInternResponseBuilder.build();
    }
}
