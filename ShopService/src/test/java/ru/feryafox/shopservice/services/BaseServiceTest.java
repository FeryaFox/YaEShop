package ru.feryafox.shopservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.feryafox.shopservice.entitis.Shop;
import ru.feryafox.shopservice.exceptions.NoAccessToShop;
import ru.feryafox.shopservice.exceptions.ShopNotFound;
import ru.feryafox.shopservice.repositories.ShopRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BaseServiceTest {

    @Mock
    private ShopRepository shopRepository;

    @InjectMocks
    private BaseService baseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getShop: возвращает магазин при наличии в базе")
    void getShop_Found() {
        // given
        UUID shopId = UUID.randomUUID();
        Shop shop = new Shop();
        shop.setId(shopId);

        when(shopRepository.findById(shopId))
                .thenReturn(Optional.of(shop));

        // when
        Shop result = baseService.getShop(shopId);

        // then
        assertNotNull(result);
        assertEquals(shopId, result.getId());
    }

    @Test
    @DisplayName("getShop: бросает ShopNotFound, если магазин отсутствует")
    void getShop_NotFound() {
        // given
        UUID shopId = UUID.randomUUID();
        when(shopRepository.findById(shopId))
                .thenReturn(Optional.empty());

        // when / then
        assertThrows(ShopNotFound.class, () ->
                baseService.getShop(shopId));
    }

    @Test
    @DisplayName("isUserHasAccessToShop: не бросает исключение, если пользователь — владелец")
    void isUserHasAccessToShop_Owner() {
        // given
        UUID shopId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Shop shop = new Shop();
        shop.setId(shopId);
        shop.setUserOwner(userId);

        when(shopRepository.findById(shopId))
                .thenReturn(Optional.of(shop));

        // should not throw
        assertDoesNotThrow(() ->
                baseService.isUserHasAccessToShop(shopId, userId));
    }

    @Test
    @DisplayName("isUserHasAccessToShop: бросает NoAccessToShop, если пользователь не владелец")
    void isUserHasAccessToShop_NotOwner() {
        // given
        UUID shopId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Shop shop = new Shop();
        shop.setId(shopId);
        shop.setUserOwner(UUID.randomUUID()); // другой владелец

        when(shopRepository.findById(shopId))
                .thenReturn(Optional.of(shop));

        // when / then
        assertThrows(NoAccessToShop.class, () ->
                baseService.isUserHasAccessToShop(shopId, userId));
    }
}
