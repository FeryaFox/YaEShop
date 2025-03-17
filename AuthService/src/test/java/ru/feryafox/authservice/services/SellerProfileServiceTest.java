package ru.feryafox.authservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.feryafox.authservice.entities.Seller;
import ru.feryafox.authservice.entities.User;
import ru.feryafox.authservice.models.requests.UpdateSellerProfile;
import ru.feryafox.authservice.models.responses.SellerProfileResponse;
import ru.feryafox.authservice.repositories.SellerRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerProfileServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private BaseService baseService;

    @InjectMocks
    private SellerProfileService sellerProfileService;

    private User user;
    private Seller seller;
    private UpdateSellerProfile updateSellerProfile;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setPhoneNumber("+79991234567");
        user.setFirstName("Иван");
        user.setSurname("Петров");
        user.setMiddleName("Сергеевич");

        seller = new Seller();
        seller.setId(user.getId());
        seller.setUser(user);
        seller.setFullName("Иван Петров Сергеевич");
        seller.setShopName("Лучший магазин");
        seller.setBusinessLicense("BUS-123456");

        updateSellerProfile = new UpdateSellerProfile();
        updateSellerProfile.setShopName("Новый магазин");
        updateSellerProfile.setBusinessLicense("BUS-654321");
    }

    @Test
    void getSellerProfile_SellerExists_ShouldReturnProfile() {
        when(baseService.getUser(user.getPhoneNumber())).thenReturn(user);
        when(sellerRepository.findById(user.getId())).thenReturn(Optional.of(seller));

        SellerProfileResponse response = sellerProfileService.getSellerProfile(user.getPhoneNumber());

        assertNotNull(response);
        assertEquals(user.getId().toString(), response.getId());
        assertEquals(user.getPhoneNumber(), response.getPhone());
        assertEquals(seller.getShopName(), response.getShopName());
        assertEquals(seller.getBusinessLicense(), response.getBusinessLicense());

        verify(baseService, times(1)).getUser(user.getPhoneNumber());
        verify(sellerRepository, times(1)).findById(user.getId());
    }

    @Test
    void getSellerProfile_SellerNotExists_ShouldReturnUserProfileOnly() {
        when(baseService.getUser(user.getPhoneNumber())).thenReturn(user);
        when(sellerRepository.findById(user.getId())).thenReturn(Optional.empty());

        SellerProfileResponse response = sellerProfileService.getSellerProfile(user.getPhoneNumber());

        assertNotNull(response);
        assertEquals(user.getId().toString(), response.getId());
        assertEquals(user.getPhoneNumber(), response.getPhone());
        assertNull(response.getShopName());
        assertNull(response.getBusinessLicense());

        verify(baseService, times(1)).getUser(user.getPhoneNumber());
        verify(sellerRepository, times(1)).findById(user.getId());
    }

    @Test
    void updateSellerProfile_SellerExists_ShouldUpdateAndSave() {
        when(baseService.getSellerOrNull(user.getPhoneNumber())).thenReturn(seller);

        sellerProfileService.updateSellerProfile(user.getPhoneNumber(), updateSellerProfile);

        assertEquals("Сергеевич Иван Петров", seller.getFullName());
        assertEquals(updateSellerProfile.getShopName(), seller.getShopName());
        assertEquals(updateSellerProfile.getBusinessLicense(), seller.getBusinessLicense());

        verify(sellerRepository, times(1)).save(seller);
    }

    @Test
    void updateSellerProfile_SellerNotExists_ShouldCreateNewSeller() {
        when(baseService.getSellerOrNull(user.getPhoneNumber())).thenReturn(null);
        when(baseService.getUser(user.getPhoneNumber())).thenReturn(user);

        sellerProfileService.updateSellerProfile(user.getPhoneNumber(), updateSellerProfile);

        verify(sellerRepository, times(1)).save(any(Seller.class));
    }
}
