package ru.feryafox.productservice.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class ProductInfoResponse {
    private String id;
    private String name;
    private String description;
    private String shopId;
    private Set<ImageResponse> images;
    private Map<String, String> attributes;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImageResponse {
       private String url;
       private int position;

       public static ImageResponse fromImage(Image image) {
           ImageResponse imageResponse = new ImageResponse();
           imageResponse.setUrl(image.getImageUrl());
           imageResponse.setPosition(image.getPosition());

           return imageResponse;
       }
    }

    public static ProductInfoResponse from(Product product) {
        ProductInfoResponse productInfoResponse = new ProductInfoResponse();
        productInfoResponse.setId(product.getId());
        productInfoResponse.setName(product.getName());
        productInfoResponse.setDescription(product.getDescription());
        productInfoResponse.setShopId(product.getShop().getId());
        productInfoResponse.setAttributes(product.getAttributes());
        productInfoResponse.setImages(product.getImages().stream()
                .map(ImageResponse::fromImage)
                .sorted(Comparator.comparingInt(ImageResponse::getPosition))
                .collect(Collectors.toCollection(LinkedHashSet::new))
        );

        return productInfoResponse;
    }
}
