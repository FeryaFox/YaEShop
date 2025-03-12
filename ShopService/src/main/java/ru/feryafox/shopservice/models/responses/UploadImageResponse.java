package ru.feryafox.shopservice.models.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ после загрузки изображения")
public class UploadImageResponse {

    @Schema(description = "URL загруженного изображения", example = "https://example.com/uploaded-image.jpg")
    private String url;
}
