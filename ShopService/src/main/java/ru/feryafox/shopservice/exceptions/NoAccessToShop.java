package ru.feryafox.shopservice.exceptions;

public class NoAccessToShop extends ShopException{
    public NoAccessToShop(String userId, String shopId) {
        super(String.format("No access to shop with id %s and shop id %s", userId, shopId));
    }
}
