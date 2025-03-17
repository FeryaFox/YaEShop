package ru.feryafox.productservice.models.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.feryafox.models.internal.responses.ProductInfoInternResponse;
import ru.feryafox.productservice.entities.mongo.Image;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Изображение продукта")
public class ImageResponse {

    @Schema(description = "URL изображения", example = "https://example.com/image1.jpg")
    private String url;

    @Schema(description = "Позиция изображения в списке", example = "1")
    private int position;

    public static ImageResponse fromImage(Image image) {
        ImageResponse imageResponse = new ImageResponse();
        imageResponse.setUrl(image.getImageUrl());
        imageResponse.setPosition(image.getPosition());
        return imageResponse;
    }

    public static ProductInfoInternResponse.ImageInternResponse toIntern(ImageResponse imageResponse) {
        return ProductInfoInternResponse.ImageInternResponse.builder()
                .url(imageResponse.getUrl())
                .position(imageResponse.getPosition())
                .build();
    }
}
