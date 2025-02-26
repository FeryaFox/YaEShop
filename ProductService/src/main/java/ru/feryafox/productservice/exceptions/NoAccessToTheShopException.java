package ru.feryafox.productservice.exceptions;

public class NoAccessToTheShopException extends ProductException {
    public NoAccessToTheShopException(String shopId, String userId) {
        super("No access to the shop '" + shopId + "' by user '" + userId + "'");
    }
}
