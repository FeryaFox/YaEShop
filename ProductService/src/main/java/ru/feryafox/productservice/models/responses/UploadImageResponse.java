package ru.feryafox.productservice.models.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ на загрузку изображения")
public class UploadImageResponse {

    @Schema(description = "URL загруженного изображения", example = "https://example.com/uploads/image1.jpg")
    private String url;

    @Schema(description = "Позиция изображения в списке", example = "1")
    private int position;
}
