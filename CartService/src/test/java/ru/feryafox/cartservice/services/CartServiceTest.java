package ru.feryafox.cartservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.feryafox.cartservice.entities.Cart;
import ru.feryafox.cartservice.entities.CartItem;
import ru.feryafox.cartservice.entities.Product;
import ru.feryafox.cartservice.exceptions.NoCartException;
import ru.feryafox.cartservice.exceptions.ProductInTheCartIsNotExistException;
import ru.feryafox.cartservice.models.requests.PatchCartRequest;
import ru.feryafox.cartservice.models.responses.CartInfoResponse;
import ru.feryafox.cartservice.repositories.CartRepository;
import ru.feryafox.cartservice.repositories.ProductRepository;
import ru.feryafox.cartservice.services.kafka.KafkaService;
import ru.feryafox.models.internal.requests.AddToCartRequest;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private BaseService baseService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private KafkaService kafkaService;

    @InjectMocks
    private CartService cartService;

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = Cart.builder().userId("user1").items(new HashSet<>()).build();
    }

    @Test
    void testAddToCart_NewItem() {
        AddToCartRequest request = new AddToCartRequest("product1", 2);
        when(baseService.getOrCreateCartByUserId("user1")).thenReturn(cart);
        when(productRepository.findById("product1")).thenReturn(Optional.of(new Product("product1", "Test Product", "shop1", "owner1", BigDecimal.TEN)));
        when(cartRepository.save(any())).thenReturn(cart);

        cartService.addToCart("user1", 2, request);

        assertFalse(cart.getItems().isEmpty());
    }

    @Test
    void testPatchCart_ExistingProduct() {
        cart.getItems().add(new CartItem("product1", 2, BigDecimal.TEN, "shop1"));
        when(baseService.getCartOrNullByUserId("user1")).thenReturn(cart);
        PatchCartRequest patchRequest = new PatchCartRequest(5);

        cartService.patchCart("product1", "user1", patchRequest);

        assertEquals(5, cart.getItems().iterator().next().getQuantity());
    }

    @Test
    void testPatchCart_ProductNotInCart() {
        when(baseService.getCartOrNullByUserId("user1")).thenReturn(cart);
        PatchCartRequest patchRequest = new PatchCartRequest(5);

        assertThrows(ProductInTheCartIsNotExistException.class, () ->
                cartService.patchCart("product1", "user1", patchRequest));
    }

    @Test
    void testDeleteProductFromCart() {
        cart.getItems().add(new CartItem("product1", 2, BigDecimal.TEN, "shop1"));
        when(baseService.getCartOrNullByUserId("user1")).thenReturn(cart);

        cartService.deleteProductFromCart("product1", "user1");

        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testClearCart() {
        cart.getItems().add(new CartItem("product1", 2, BigDecimal.TEN, "shop1"));
        when(baseService.getCartOrNullByUserId("user1")).thenReturn(cart);

        cartService.clearCart("user1");

        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testCreateOrder() {
        cart.getItems().add(new CartItem("product1", 2, BigDecimal.TEN, "shop1"));
        when(baseService.getCartOrNullByUserId("user1")).thenReturn(cart);
        doNothing().when(kafkaService).sendOrderEvent(any());

        cartService.createOrder("user1");

        verify(kafkaService, times(1)).sendOrderEvent(any());
        assertTrue(cart.getItems().isEmpty());
    }
}
