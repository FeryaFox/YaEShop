package ru.feryafox.productservice.exceptions;

public class ShopIsNotExist extends RuntimeException{
    public ShopIsNotExist(String shopId){
        super("Shop "+shopId+" is not exist");
    }
}
