package ru.feryafox.productservice.exceptions;

public class ShopAndProductDontLinkedException extends ProductException {
    public ShopAndProductDontLinkedException(String productId, String shopId) {
        super("Product " + productId + " don't linked to shop " + shopId);
    }
}
