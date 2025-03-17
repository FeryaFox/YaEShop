package ru.feryafox.productservice.services;

import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.feryafox.kafka.models.ProductEvent;
import ru.feryafox.kafka.models.ReviewEvent;
import ru.feryafox.models.internal.requests.AddToCartRequest;
import ru.feryafox.models.internal.responses.ProductInfoInternResponse;
import ru.feryafox.productservice.entities.mongo.Image;
import ru.feryafox.productservice.entities.mongo.Product;
import ru.feryafox.productservice.entities.mongo.Shop;
import ru.feryafox.productservice.exceptions.ShopAndProductDontLinkedException;
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

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ShopRepository shopRepository;
    private final BaseService baseService;
    private final ProductRepository productRepository;
    private final MinioService minioService;
    private final ImageRepository imageRepository;
    private final FeignService feignService;
    private final KafkaProducerService kafkaProducerService;
    private final KafkaService kafkaService;

    @Transactional
    public CreateProductResponse createProduct(CreateProductRequest createProductRequest, String userId) {
        log.info("Создание продукта в магазине {} пользователем {}", createProductRequest.getShopId(), userId);

        Shop shop = shopRepository.findById(createProductRequest.getShopId())
                .orElseGet(() -> getAndSaveShopFromShopService(createProductRequest.getShopId()));

        baseService.isUserHasAccessToShop(shop, userId);

        Product product = Product.from(createProductRequest);
        product.setShop(shop);
        product.setUserCreate(userId);

        shop.getProducts().add(product);
        product = productRepository.save(product);
        shopRepository.save(shop);

        log.info("Продукт {} успешно создан в магазине {}", product.getId(), shop.getId());

        ProductEvent productEvent = kafkaService.convertProductToEvent(product, ProductEvent.ProductStatus.CREATED);
        kafkaProducerService.sendProductEvent(productEvent);

        return new CreateProductResponse(product.getId());
    }

    public Shop getAndSaveShopFromShopService(String shopId) {
        log.info("Получение информации о магазине {} через Feign", shopId);
        try {
            var resp = feignService.getShopInfo(shopId);
            Shop shop = Shop.from(resp);
            shop = shopRepository.save(shop);
            log.info("Магазин {} успешно сохранен в базе", shopId);
            return shop;
        } catch (FeignException.NotFound e) {
            log.error("Ошибка: Магазин {} не найден через Feign", shopId, e);
            throw new ShopIsNotExist(shopId);
        }
    }

    public UploadImageResponse uploadImage(MultipartFile file, String productId, String userId) throws Exception {
        log.info("Загрузка изображения для продукта {} пользователем {}", productId, userId);

        baseService.isUserHasAccessToProduct(productId, userId);
        String url = minioService.uploadImage(file);
        Product product = baseService.getProduct(productId);
        int position = product.getImages().size() + 1;

        Image image = new Image();
        image.setImageUrl(url);
        image.setProduct(product);
        image.setPosition(position);
        image.setUploadedUser(userId);

        imageRepository.save(image);
        product.getImages().add(image);
        productRepository.save(product);

        log.info("Изображение загружено по URL {} для продукта {}", url, productId);

        return new UploadImageResponse(url, position);
    }

    public ProductInfoResponse getProductInfo(String productId) {
        log.info("Получение информации о продукте {}", productId);
        Product product = baseService.getProduct(productId);
        return ProductInfoResponse.from(product);
    }

    public List<ProductInfoResponse> getProductInfoFromShop(String shopId, int page, int size) {
        log.info("Получение списка продуктов для магазина {} с пагинацией page={}, size={}", shopId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        List<Product> products = productRepository.findAllByShop_Id(shopId, pageable);
        return products.stream().map(ProductInfoResponse::from).toList();
    }
    public List<ProductInfoInternResponse> getProductInfoIntern(String shopId, int page, int size) {
        log.info("Получение списка продуктов для магазина внутренний {} с пагинацией page={}, size={}", shopId, page, size);
        var products = getProductInfoFromShop(shopId, page, size);

        return products.stream()
                .map(ProductInfoResponse::toIntern)
                .toList();

    }
    @Transactional
    public Product updateProduct(String productId, UpdateProductRequest updateRequest, String userId) {
        log.info("Обновление продукта {} пользователем {}", productId, userId);
        Product product = baseService.getProduct(productId);
        baseService.isUserHasAccessToProduct(productId, userId);

        product.setName(updateRequest.getName());
        product.setDescription(updateRequest.getDescription());
        product.setPrice(updateRequest.getPrice());
        product.setAttributes(updateRequest.getAttributes());

        product = productRepository.save(product);
        log.info("Продукт {} успешно обновлен", productId);

        ProductEvent productEvent = kafkaService.convertProductToEvent(product, ProductEvent.ProductStatus.UPDATED);
        kafkaProducerService.sendProductEvent(productEvent);

        return product;
    }

    @Transactional
    public void updateProductRating(ReviewEvent reviewEvent) {
        log.info("Обновление рейтинга продукта {} по событию ReviewEvent", reviewEvent.getProductId());
        var product = baseService.getProduct(reviewEvent.getProductId());

        if (!product.getShop().getId().equals(reviewEvent.getShopId())) {
            log.error("Ошибка: Продукт {} не принадлежит магазину {}", reviewEvent.getProductId(), reviewEvent.getShopId());
            throw new ShopAndProductDontLinkedException(reviewEvent.getProductId(), reviewEvent.getShopId());
        }

        product.setProductRating(reviewEvent.getAvgProductRating());
        product.setCountProductReviews(reviewEvent.getCountProductReviews());

        productRepository.save(product);
        log.info("Рейтинг продукта {} успешно обновлен", reviewEvent.getProductId());
    }

    public void addToCart(String productId, int quantity, String userId) {
        log.info("Добавление продукта {} в корзину пользователя {} (количество: {})", productId, userId, quantity);
        var product = baseService.getProduct(productId);
        var request = new AddToCartRequest(productId, product.getPrice().doubleValue());
        feignService.addToCart(userId, quantity, request);
    }

    public List<ProductInfoResponse> searchProductsByName(String name, int page, int size) {
        if (name == null || name.trim().isEmpty()) {
            log.warn("Поиск продуктов с пустым именем невозможен");
            throw new IllegalArgumentException("Имя продукта не может быть пустым");
        }

        if (page < 0 || size <= 0) {
            log.warn("Некорректные параметры пагинации: page={}, size={}", page, size);
            throw new IllegalArgumentException("Параметры пагинации должны быть положительными");
        }

        log.info("Поиск продуктов по имени '{}' (page={}, size={})", name, page, size);

        Pageable pageable = PageRequest.of(page, size);
        List<Product> products = productRepository.findAllByNameContainingIgnoreCase(name, pageable);

        if (products.isEmpty()) {
            log.warn("Поиск продуктов по имени '{}' не дал результатов", name);
        } else {
            log.info("Найдено {} продуктов по имени '{}'", products.size(), name);
        }

        return products.stream()
                .map(ProductInfoResponse::from)
                .toList();
    }
}
