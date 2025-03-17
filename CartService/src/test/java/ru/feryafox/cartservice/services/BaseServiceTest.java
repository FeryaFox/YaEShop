package ru.feryafox.cartservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.feryafox.cartservice.entities.Cart;
import ru.feryafox.cartservice.repositories.CartRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private BaseService baseService;

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = Cart.builder().userId("user1").build();
    }

    @Test
    void testGetOrCreateCartByUserId_CartExists() {
        when(cartRepository.findByUserId("user1")).thenReturn(Optional.of(cart));

        Cart result = baseService.getOrCreateCartByUserId("user1");

        assertNotNull(result);
        assertEquals("user1", result.getUserId());
    }

    @Test
    void testGetOrCreateCartByUserId_CartDoesNotExist() {
        when(cartRepository.findByUserId("user1")).thenReturn(Optional.empty());

        Cart result = baseService.getOrCreateCartByUserId("user1");

        assertNotNull(result);
        assertEquals("user1", result.getUserId());
    }

    @Test
    void testGetCartOrNullByUserId_CartExists() {
        when(cartRepository.findByUserId("user1")).thenReturn(Optional.of(cart));

        Cart result = baseService.getCartOrNullByUserId("user1");

        assertNotNull(result);
        assertEquals("user1", result.getUserId());
    }

    @Test
    void testGetCartOrNullByUserId_CartDoesNotExist() {
        when(cartRepository.findByUserId("user1")).thenReturn(Optional.empty());

        Cart result = baseService.getCartOrNullByUserId("user1");

        assertNull(result);
    }
}
