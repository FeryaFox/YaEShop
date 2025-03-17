package ru.feryafox.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Структура ответа при ошибке")
public class ErrorResponse {

    @Schema(description = "Код ошибки", example = "some_error")
    private String error;

    @Schema(description = "Описание ошибки", example = "Детальное описание ошибки")
    private String message;
}
