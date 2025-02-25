package ru.feryafox.productservice.exceptions;

public class ProductIsNotExist extends ShopIsNotExist{
    public ProductIsNotExist(String productId) {
        super("Product is not exist" + productId);
    }
}
