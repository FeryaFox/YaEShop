package ru.feryafox.shopservice.exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.feryafox.exceptions.ErrorMessage;

import java.util.UUID;

@Slf4j
@Getter
public class ShopNotFound extends ShopException{

    private final UUID shopId;
    private static final String MESSAGE_TEMPLATE = "Получаемый %s магазин не существует";
    public static final String JSON_ERROR = "Shop not found";
    public static final String JSON_ERROR_MESSAGE =  "Shop %s not found";
    public static ShopNotFoundMessage MESSAGE ;
    public static final String JSON_MESSAGE = "{\"error\":\"" + JSON_ERROR + "\",\"message\":\"" + JSON_ERROR_MESSAGE + "\"}";

    public ShopNotFound(UUID shopId) {
        super(String.format(MESSAGE_TEMPLATE, shopId));
        this.shopId = shopId;
        MESSAGE = new ShopNotFoundMessage(JSON_ERROR, JSON_ERROR_MESSAGE, shopId);
    }

    @Getter
    public static class ShopNotFoundMessage extends ErrorMessage {
        public ShopNotFoundMessage(String error, String message, UUID shopId) {
            super(error, String.format(message, shopId));
        }
    }
}
