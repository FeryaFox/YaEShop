package ru.feryafox.authservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.feryafox.authservice.entities.Buyer;
import ru.feryafox.authservice.entities.User;
import ru.feryafox.authservice.models.requests.UpdateBuyerProfile;
import ru.feryafox.authservice.models.responses.BuyerProfileResponse;
import ru.feryafox.authservice.repositories.BuyerRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuyerProfileServiceTest {

    @Mock
    private BuyerRepository buyerRepository;

    @Mock
    private BaseService baseService;

    @InjectMocks
    private BuyerProfileService buyerProfileService;

    private User user;
    private Buyer buyer;
    private UpdateBuyerProfile updateBuyerProfile;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setPhoneNumber("+79991234567");
        user.setFirstName("Иван");
        user.setSurname("Петров");
        user.setMiddleName("Сергеевич");

        buyer = new Buyer();
        buyer.setId(user.getId());
        buyer.setUser(user);
        buyer.setFullName("Иван Петров Сергеевич");
        buyer.setAddress("ул. Ленина, д. 10, кв. 5");
        buyer.setDateOfBirth(LocalDate.of(1990, 6, 15));

        updateBuyerProfile = new UpdateBuyerProfile();
        updateBuyerProfile.setAddress("ул. Новая, д. 25, кв. 8");
        updateBuyerProfile.setDateOfBirth(LocalDate.of(1991, 7, 20));
    }

    @Test
    void getBuyerProfileService_BuyerExists_ShouldReturnProfile() {
        when(baseService.getUser(user.getPhoneNumber())).thenReturn(user);
        when(buyerRepository.findById(user.getId())).thenReturn(Optional.of(buyer));

        BuyerProfileResponse response = buyerProfileService.getBuyerProfileService(user.getPhoneNumber());

        assertNotNull(response);
        assertEquals(user.getId().toString(), response.getId());
        assertEquals(user.getPhoneNumber(), response.getPhone());
        assertEquals(buyer.getAddress(), response.getAddress());

        verify(baseService, times(1)).getUser(user.getPhoneNumber());
        verify(buyerRepository, times(1)).findById(user.getId());
    }

    @Test
    void getBuyerProfileService_BuyerNotExists_ShouldReturnUserProfileOnly() {
        when(baseService.getUser(user.getPhoneNumber())).thenReturn(user);
        when(buyerRepository.findById(user.getId())).thenReturn(Optional.empty());

        BuyerProfileResponse response = buyerProfileService.getBuyerProfileService(user.getPhoneNumber());

        assertNotNull(response);
        assertEquals(user.getId().toString(), response.getId());
        assertEquals(user.getPhoneNumber(), response.getPhone());
        assertNull(response.getAddress());

        verify(baseService, times(1)).getUser(user.getPhoneNumber());
        verify(buyerRepository, times(1)).findById(user.getId());
    }

    @Test
    void updateBuyerProfile_BuyerExists_ShouldUpdateAndSave() {
        when(baseService.getBuyerOrNull(user.getPhoneNumber())).thenReturn(buyer);

        buyerProfileService.updateBuyerProfile(user.getPhoneNumber(), updateBuyerProfile);

        assertEquals("Сергеевич Иван Петров", buyer.getFullName());
        assertEquals(updateBuyerProfile.getAddress(), buyer.getAddress());
        assertEquals(updateBuyerProfile.getDateOfBirth(), buyer.getDateOfBirth());

        verify(buyerRepository, times(1)).save(buyer);
    }

    @Test
    void updateBuyerProfile_BuyerNotExists_ShouldCreateNewBuyer() {
        when(baseService.getBuyerOrNull(user.getPhoneNumber())).thenReturn(null);
        when(baseService.getUser(user.getPhoneNumber())).thenReturn(user);

        buyerProfileService.updateBuyerProfile(user.getPhoneNumber(), updateBuyerProfile);

        verify(buyerRepository, times(1)).save(any(Buyer.class));
    }
}
