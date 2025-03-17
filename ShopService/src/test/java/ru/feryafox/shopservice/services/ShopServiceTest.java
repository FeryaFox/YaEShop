package ru.feryafox.shopservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;
import ru.feryafox.kafka.models.ShopEvent;
import ru.feryafox.kafka.models.ShopRatingEvent;
import ru.feryafox.models.internal.responses.ShopInfoInternalResponse;
import ru.feryafox.shopservice.entitis.Shop;
import ru.feryafox.shopservice.exceptions.NoAccessToShop;
import ru.feryafox.shopservice.models.requests.CreateShopRequest;
import ru.feryafox.shopservice.models.requests.UpdateShopRequest;
import ru.feryafox.shopservice.models.responses.CreateShopResponse;
import ru.feryafox.shopservice.models.responses.ShopInfoResponse;
import ru.feryafox.shopservice.models.responses.UploadImageResponse;
import ru.feryafox.shopservice.repositories.ShopRepository;
import ru.feryafox.shopservice.services.kafka.KafkaProducerService;
import ru.feryafox.shopservice.services.minio.MinioService;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShopServiceTest {

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private BaseService baseService;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private ShopService shopService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("createShop: Создает магазин и отправляет событие в Kafka")
    void createShop_Success() {
        // given
        String userId = UUID.randomUUID().toString();
        CreateShopRequest request = new CreateShopRequest("My Shop", "Some description");

        Shop savedShop = Shop.builder()
                .id(UUID.randomUUID())
                .shopName("My Shop")
                .shopDescription("Some description")
                .userOwner(UUID.fromString(userId))
                .build();

        when(shopRepository.save(any(Shop.class))).thenReturn(savedShop);

        // when
        CreateShopResponse response = shopService.createShop(request, userId);

        // then
        assertNotNull(response);
        assertEquals(savedShop.getId().toString(), response.getShopId());

        // Проверяем, что событие отправлено
        verify(kafkaProducerService, times(1)).sendShopUpdate(any(ShopEvent.class));
        // И что магазин был сохранен
        verify(shopRepository, times(1)).save(any(Shop.class));
    }

    @Test
    @DisplayName("getShopInfo: Возвращает информацию о магазине")
    void getShopInfo_Success() {
        // given
        UUID shopId = UUID.randomUUID();
        Shop shop = Shop.builder()
                .id(shopId)
                .shopName("Test Shop")
                .shopDescription("Description")
                .rating(BigDecimal.valueOf(4.5))
                .build();

        when(baseService.getShop(shopId)).thenReturn(shop);

        // when
        ShopInfoResponse response = shopService.getShopInfo(shopId);

        // then
        assertNotNull(response);
        assertEquals(shopId.toString(), response.getShopId());
        assertEquals("Test Shop", response.getName());
        assertEquals(4.5, response.getRating());
    }

    @Test
    @DisplayName("getInternalShopInfo: Возвращает внутреннюю информацию о магазине")
    void getInternalShopInfo_Success() {
        // given
        UUID shopId = UUID.randomUUID();
        Shop shop = Shop.builder()
                .id(shopId)
                .shopName("Internal Shop")
                .userOwner(UUID.randomUUID())
                .build();

        when(baseService.getShop(shopId)).thenReturn(shop);

        // when
        ShopInfoInternalResponse response = shopService.getInternalShopInfo(shopId);

        // then
        assertNotNull(response);
        assertEquals(shopId.toString(), response.getShopId());
        assertEquals("Internal Shop", response.getName());
    }

    @Test
    @DisplayName("updateShopRatingFromEvent: Обновляет рейтинг в базе")
    void updateShopRatingFromEvent_Success() {
        // given
        ShopRatingEvent ratingEvent = new ShopRatingEvent();
        ratingEvent.setShopId("123e4567-e89b-12d3-a456-426614174000");
        ratingEvent.setShopRating(4.2);

        Shop shop = Shop.builder()
                .id(UUID.fromString(ratingEvent.getShopId()))
                .build();

        when(baseService.getShop(UUID.fromString(ratingEvent.getShopId())))
                .thenReturn(shop);

        // when
        shopService.updateShopRatingFromEvent(ratingEvent);

        // then
        verify(shopRepository, times(1)).save(shop);
        assertEquals(BigDecimal.valueOf(4.2), shop.getRating());
    }

    @Test
    @DisplayName("uploadImage: Успешно загружает изображение и обновляет путь в магазине")
    void uploadImage_Success() throws Exception {
        // given
        UUID shopId = UUID.randomUUID();
        String userId = UUID.randomUUID().toString();

        Shop shop = Shop.builder()
                .id(shopId)
                .userOwner(UUID.fromString(userId))
                .build();

        MultipartFile file = mock(MultipartFile.class);

        when(baseService.getShop(shopId)).thenReturn(shop);
        doNothing().when(baseService).isUserHasAccessToShop(shopId, UUID.fromString(userId));
        when(minioService.uploadImage(any(MultipartFile.class))).thenReturn("https://minio.url/my-image.png");

        // when
        UploadImageResponse response = shopService.uploadImage(file, shopId, userId);

        // then
        assertNotNull(response);
        assertEquals("https://minio.url/my-image.png", response.getUrl());
        verify(shopRepository, times(1)).save(any(Shop.class));
    }

    @Test
    @DisplayName("uploadImage: Бросает RuntimeException при ошибке загрузки")
    void uploadImage_Exception() throws Exception {
        // given
        UUID shopId = UUID.randomUUID();
        String userId = UUID.randomUUID().toString();
        MultipartFile file = mock(MultipartFile.class);

        Shop shop = Shop.builder().id(shopId).userOwner(UUID.fromString(userId)).build();
        when(baseService.getShop(shopId)).thenReturn(shop);
        doNothing().when(baseService).isUserHasAccessToShop(shopId, UUID.fromString(userId));

        // Эмулируем ошибку в minioService
        when(minioService.uploadImage(file)).thenThrow(new RuntimeException("Ошибка при загрузке"));

        // when / then
        assertThrows(RuntimeException.class, () ->
                shopService.uploadImage(file, shopId, userId)
        );
        // Проверяем, что метод репозитория не был вызван
        verify(shopRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateShop: Успешно обновляет магазин и отправляет событие")
    void updateShop_Success() {
        // given
        UUID shopId = UUID.randomUUID();
        String userId = UUID.randomUUID().toString();
        UpdateShopRequest request = new UpdateShopRequest("Updated name", "New desc");

        Shop shop = Shop.builder()
                .id(shopId)
                .userOwner(UUID.fromString(userId))
                .build();

        doNothing().when(baseService).isUserHasAccessToShop(shopId, UUID.fromString(userId));
        when(baseService.getShop(shopId)).thenReturn(shop);

        // when
        shopService.updateShop(request, shopId, userId);

        // then
        verify(shopRepository, times(1)).save(shop);
        verify(kafkaProducerService, times(1)).sendShopUpdate(any(ShopEvent.class));
        assertEquals("Updated name", shop.getShopName());
        assertEquals("New desc", shop.getShopDescription());
    }

    @Test
    @DisplayName("updateShop: Бросает NoAccessToShop при отсутствии доступа")
    void updateShop_NoAccess() {
        // given
        UUID shopId = UUID.randomUUID();
        String userId = UUID.randomUUID().toString();
        UpdateShopRequest request = new UpdateShopRequest("Name", "Desc");

        // Эмулируем, что проверка доступа выбросит исключение
        doThrow(new NoAccessToShop(userId, shopId.toString()))
                .when(baseService).isUserHasAccessToShop(shopId, UUID.fromString(userId));

        // when / then
        assertThrows(NoAccessToShop.class, () ->
                shopService.updateShop(request, shopId, userId)
        );

        verify(shopRepository, never()).save(any());
        verify(kafkaProducerService, never()).sendShopUpdate(any());
    }
}
