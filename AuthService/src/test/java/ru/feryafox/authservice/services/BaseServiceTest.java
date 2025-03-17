package ru.feryafox.authservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.feryafox.authservice.entities.Buyer;
import ru.feryafox.authservice.entities.Seller;
import ru.feryafox.authservice.entities.User;
import ru.feryafox.authservice.exceptions.user.UserIsNotExistException;
import ru.feryafox.authservice.repositories.BuyerRepository;
import ru.feryafox.authservice.repositories.SellerRepository;
import ru.feryafox.authservice.repositories.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BuyerRepository buyerRepository;

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private BaseService baseService;

    private User user;
    private Buyer buyer;
    private Seller seller;
    private UUID userId;
    private String phoneNumber;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        phoneNumber = "+79991234567";

        user = new User();
        user.setId(userId);
        user.setPhoneNumber(phoneNumber);

        buyer = new Buyer();
        buyer.setId(userId);
        buyer.setUser(user);

        seller = new Seller();
        seller.setId(userId);
        seller.setUser(user);
    }

    @Test
    void getUser_UserExists_ShouldReturnUser() {
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(user));

        User result = baseService.getUser(phoneNumber);

        assertNotNull(result);
        assertEquals(userId, result.getId());

        verify(userRepository, times(1)).findByPhoneNumber(phoneNumber);
    }

    @Test
    void getUser_UserNotExists_ShouldThrowException() {
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        assertThrows(UserIsNotExistException.class, () -> baseService.getUser(phoneNumber));

        verify(userRepository, times(1)).findByPhoneNumber(phoneNumber);
    }

    @Test
    void getBuyer_BuyerExists_ShouldReturnBuyer() {
        when(buyerRepository.findByUser_PhoneNumber(phoneNumber)).thenReturn(Optional.of(buyer));

        Buyer result = baseService.getBuyer(phoneNumber);

        assertNotNull(result);
        assertEquals(userId, result.getId());

        verify(buyerRepository, times(1)).findByUser_PhoneNumber(phoneNumber);
    }

    @Test
    void getBuyer_BuyerNotExists_ShouldThrowException() {
        when(buyerRepository.findByUser_PhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        assertThrows(UserIsNotExistException.class, () -> baseService.getBuyer(phoneNumber));

        verify(buyerRepository, times(1)).findByUser_PhoneNumber(phoneNumber);
    }

    @Test
    void getBuyerOrNull_BuyerExists_ShouldReturnBuyer() {
        when(buyerRepository.findByUser_PhoneNumber(phoneNumber)).thenReturn(Optional.of(buyer));

        Buyer result = baseService.getBuyerOrNull(phoneNumber);

        assertNotNull(result);
        assertEquals(userId, result.getId());

        verify(buyerRepository, times(1)).findByUser_PhoneNumber(phoneNumber);
    }

    @Test
    void getBuyerOrNull_BuyerNotExists_ShouldReturnNull() {
        when(buyerRepository.findByUser_PhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        Buyer result = baseService.getBuyerOrNull(phoneNumber);

        assertNull(result);

        verify(buyerRepository, times(1)).findByUser_PhoneNumber(phoneNumber);
    }

    @Test
    void getSellerOrNull_SellerExists_ShouldReturnSeller() {
        when(sellerRepository.findByUser_PhoneNumber(phoneNumber)).thenReturn(Optional.of(seller));

        Seller result = baseService.getSellerOrNull(phoneNumber);

        assertNotNull(result);
        assertEquals(userId, result.getId());

        verify(sellerRepository, times(1)).findByUser_PhoneNumber(phoneNumber);
    }

    @Test
    void getSellerOrNull_SellerNotExists_ShouldReturnNull() {
        when(sellerRepository.findByUser_PhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        Seller result = baseService.getSellerOrNull(phoneNumber);

        assertNull(result);

        verify(sellerRepository, times(1)).findByUser_PhoneNumber(phoneNumber);
    }
}
