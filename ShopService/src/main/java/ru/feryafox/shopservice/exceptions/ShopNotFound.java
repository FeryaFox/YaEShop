package ru.feryafox.shopservice.exceptions;

import java.util.UUID;

public class ShopNotFound extends ShopException{
    public ShopNotFound(UUID shopId) {
        super(String.format("Магазин %s не найден", shopId));
    }
}
