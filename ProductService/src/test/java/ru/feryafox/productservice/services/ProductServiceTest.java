package ru.feryafox.productservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ru.feryafox.kafka.models.ProductEvent;
import ru.feryafox.kafka.models.ReviewEvent;
import ru.feryafox.models.internal.requests.AddToCartRequest;
import ru.feryafox.models.internal.responses.ProductInfoInternResponse;
import ru.feryafox.productservice.entities.mongo.Image;
import ru.feryafox.productservice.entities.mongo.Product;
import ru.feryafox.productservice.entities.mongo.Shop;
import ru.feryafox.productservice.exceptions.ShopIsNotExist;
import ru.feryafox.productservice.models.requests.CreateProductRequest;
import ru.feryafox.productservice.models.requests.UpdateProductRequest;
import ru.feryafox.productservice.models.responses.CreateProductResponse;
import ru.feryafox.productservice.models.responses.ProductInfoResponse;
import ru.feryafox.productservice.models.responses.UploadImageResponse;
import ru.feryafox.productservice.repositories.mongo.ImageRepository;
import ru.feryafox.productservice.repositories.mongo.ProductRepository;
import ru.feryafox.productservice.repositories.mongo.ShopRepository;
import ru.feryafox.productservice.services.feign.FeignService;
import ru.feryafox.productservice.services.kafka.KafkaProducerService;
import ru.feryafox.productservice.services.kafka.KafkaService;
import ru.feryafox.productservice.services.minio.MinioService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ShopRepository shopRepository;
    @Mock
    private BaseService baseService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MinioService minioService;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private FeignService feignService;
    @Mock
    private KafkaProducerService kafkaProducerService;
    @Mock
    private KafkaService kafkaService;
    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ProductService productService;

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
        product.setPrice(BigDecimal.valueOf(100.0));
    }

    @Test
    void createProduct_ShouldCreateProductSuccessfully() {
        CreateProductRequest request = new CreateProductRequest("Test Product", "Description", 100.0, "shop123", null);

        when(shopRepository.findById("shop123")).thenReturn(Optional.of(shop));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(kafkaService.convertProductToEvent(any(Product.class), any(ProductEvent.ProductStatus.class)))
                .thenReturn(new ProductEvent());

        CreateProductResponse response = productService.createProduct(request, "user1");

        assertNotNull(response);
        assertEquals("product123", response.getId());
        verify(kafkaProducerService, times(1)).sendProductEvent(any(ProductEvent.class));
    }

    @Test
    void getProductInfo_ShouldReturnProductInfo() {
        when(baseService.getProduct("product123")).thenReturn(product);

        ProductInfoResponse response = productService.getProductInfo("product123");
        assertNotNull(response);
        assertEquals("product123", response.getId());
    }

    @Test
    void updateProduct_ShouldUpdateProductSuccessfully() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setName("Updated Name");
        request.setDescription("Updated Description");
        request.setPrice(BigDecimal.valueOf(150.0));

        when(baseService.getProduct("product123")).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product updatedProduct = productService.updateProduct("product123", request, "user1");
        assertEquals("Updated Name", updatedProduct.getName());
        assertEquals("Updated Description", updatedProduct.getDescription());
    }

    @Test
    void uploadImage_ShouldUploadImageSuccessfully() throws Exception {
        when(baseService.getProduct("product123")).thenReturn(product);
        when(minioService.uploadImage(multipartFile)).thenReturn("http://image.url");
        when(imageRepository.save(any(Image.class))).thenReturn(new Image());

        UploadImageResponse response = productService.uploadImage(multipartFile, "product123", "user1");
        assertNotNull(response);
        assertEquals("http://image.url", response.getUrl());
    }

    @Test
    void addToCart_ShouldCallFeignService() {
        when(baseService.getProduct("product123")).thenReturn(product);
        doNothing().when(feignService).addToCart(eq("user1"), eq(2), any(AddToCartRequest.class));

        productService.addToCart("product123", 2, "user1");
        verify(feignService, times(1)).addToCart(eq("user1"), eq(2), any(AddToCartRequest.class));
    }

    @Test
    void getProductInfoFromShop_ShouldReturnProductList() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAllByShop_Id("shop123", pageable)).thenReturn(List.of(product));

        List<ProductInfoResponse> responses = productService.getProductInfoFromShop("shop123", 0, 10);
        assertEquals(1, responses.size());
        assertEquals("product123", responses.get(0).getId());
    }

    @Test
    void updateProductRating_ShouldUpdateRatingSuccessfully() {
        ReviewEvent reviewEvent = ReviewEvent.builder()
                .productId("product123")
                .shopId("shop123")
                .avgProductRating(4.5)
                .countProductReviews(10)
                .build();

        when(baseService.getProduct("product123")).thenReturn(product);

        productService.updateProductRating(reviewEvent);

        assertEquals(4.5, product.getProductRating());
        assertEquals(10, product.getCountProductReviews());
        verify(productRepository, times(1)).save(product);
    }
}
