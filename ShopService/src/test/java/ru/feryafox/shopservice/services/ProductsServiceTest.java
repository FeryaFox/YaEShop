package ru.feryafox.shopservice.services;

import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.feryafox.models.internal.responses.ProductInfoInternResponse;
import ru.feryafox.shopservice.services.feign.FeignService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductsServiceTest {

    @Mock
    private FeignService feignService;

    @InjectMocks
    private ProductsService productsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getInternProducts: Успешно получает список товаров через Feign")
    void getInternProducts_Success() {
        // given
        String shopId = "shop123";
        int page = 0;
        int size = 10;

        ProductInfoInternResponse product1 = new ProductInfoInternResponse();
        ProductInfoInternResponse product2 = new ProductInfoInternResponse();
        List<ProductInfoInternResponse> mockProducts = List.of(product1, product2);

        when(feignService.getProducts(shopId, page, size)).thenReturn(mockProducts);

        // when
        List<ProductInfoInternResponse> result =
                productsService.getInternProducts(shopId, page, size);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(feignService, times(1)).getProducts(shopId, page, size);
    }

    @Test
    @DisplayName("getInternProducts: Бросает FeignException при ошибке Feign-запроса")
    void getInternProducts_FeignError() {
        // given
        String shopId = "shop123";
        int page = 0;
        int size = 10;

        when(feignService.getProducts(shopId, page, size))
                .thenThrow(FeignException.class);

        // when / then
        assertThrows(FeignException.class, () ->
                productsService.getInternProducts(shopId, page, size)
        );
        verify(feignService, times(1)).getProducts(shopId, page, size);
    }
}
