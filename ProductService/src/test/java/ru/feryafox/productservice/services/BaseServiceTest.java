package ru.feryafox.productservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.feryafox.productservice.entities.mongo.Product;
import ru.feryafox.productservice.entities.mongo.Shop;
import ru.feryafox.productservice.exceptions.NoAccessToTheProductException;
import ru.feryafox.productservice.exceptions.NoAccessToTheShopException;
import ru.feryafox.productservice.exceptions.ProductIsNotExist;
import ru.feryafox.productservice.exceptions.ShopIsNotExist;
import ru.feryafox.productservice.repositories.mongo.ProductRepository;
import ru.feryafox.productservice.repositories.mongo.ShopRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseServiceTest {

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private BaseService baseService;

    private Shop shop;
    private Product product;

    @BeforeEach
    void setUp() {
        shop = new Shop();
        shop.setId("shop123");
        shop.setUserOwner("user1");

        product = new Product();
        product.setId("product123");
        product.setShop(shop);
        product.setUserCreate("user1");
    }

    @Test
    void getShop_ShouldReturnShop_WhenExists() {
        when(shopRepository.findById("shop123")).thenReturn(Optional.of(shop));

        Shop result = baseService.getShop("shop123");

        assertNotNull(result);
        assertEquals("shop123", result.getId());
        assertEquals("user1", result.getUserOwner());
    }

    @Test
    void getShop_ShouldThrowException_WhenShopNotExists() {
        when(shopRepository.findById("shop123")).thenReturn(Optional.empty());

        ShopIsNotExist exception = assertThrows(ShopIsNotExist.class, () -> baseService.getShop("shop123"));

        assertEquals("Shop shop123 is not exist", exception.getMessage());
    }

    @Test
    void getProduct_ShouldReturnProduct_WhenExists() {
        when(productRepository.findById("product123")).thenReturn(Optional.of(product));

        Product result = baseService.getProduct("product123");

        assertNotNull(result);
        assertEquals("product123", result.getId());
        assertEquals("user1", result.getUserCreate());
    }

    @Test
    void getProduct_ShouldThrowException_WhenProductNotExists() {
        when(productRepository.findById("product123")).thenReturn(Optional.empty());

        ProductIsNotExist exception = assertThrows(ProductIsNotExist.class, () -> baseService.getProduct("product123"));

        assertEquals("Shop Product is not existproduct123 is not exist", exception.getMessage());
    }

    @Test
    void isUserHasAccessToProduct_ShouldPass_WhenUserIsOwner() {
        when(productRepository.findById("product123")).thenReturn(Optional.of(product));

        assertDoesNotThrow(() -> baseService.isUserHasAccessToProduct("product123", "user1"));
    }

    @Test
    void isUserHasAccessToProduct_ShouldThrowException_WhenUserIsNotOwner() {
        when(productRepository.findById("product123")).thenReturn(Optional.of(product));

        NoAccessToTheProductException exception = assertThrows(
                NoAccessToTheProductException.class,
                () -> baseService.isUserHasAccessToProduct("product123", "user2")
        );

        assertEquals("No access to the product product123 by user user2", exception.getMessage());
    }

    @Test
    void isUserHasAccessToShop_ShouldPass_WhenUserIsOwner() {
        assertDoesNotThrow(() -> baseService.isUserHasAccessToShop(shop, "user1"));
    }

    @Test
    void isUserHasAccessToShop_ShouldThrowException_WhenUserIsNotOwner() {
        NoAccessToTheShopException exception = assertThrows(
                NoAccessToTheShopException.class,
                () -> baseService.isUserHasAccessToShop(shop, "user2")
        );

        assertEquals("No access to the shop 'shop123' by user 'user2'", exception.getMessage());
    }
}
