package ru.feryafox.productservice.services;

import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
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
        var shopOptional = shopRepository.findById(createProductRequest.getShopId());
        Shop shop = shopOptional.orElseGet(() -> getAndSaveShopFromShopService(createProductRequest.getShopId()));

        baseService.isUserHasAccessToShop(shop, userId);

        Product product = Product.from(createProductRequest);
        product.setShop(shop);
        product.setUserCreate(userId);

        shop.getProducts().add(product);

        product = productRepository.save(product);
        shopRepository.save(shop);
        System.out.println(product.getPrice().doubleValue());
        ProductEvent productEvent = kafkaService.convertProductToEvent(product, ProductEvent.ProductStatus.CREATED);
        kafkaProducerService.sendProductEvent(productEvent);

        // TODO добавить сюда Elasticsearch
//        ProductIndex productIndex = ProductIndex.of(product);
//        productSearchRepository.save(productIndex);

        return new CreateProductResponse(product.getId());
    }

    public Shop getAndSaveShopFromShopService(String shopId) {
        try {
            var resp = feignService.getShopInfo(shopId);
            Shop shop = Shop.from(resp);
            shop = shopRepository.save(shop);
            return shop;
        }
        catch (FeignException.NotFound e) {
            throw new ShopIsNotExist(shopId);
        }
    }

    public UploadImageResponse uploadImage(MultipartFile file, String productId, String userId) throws Exception {
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

        return new UploadImageResponse(url, position);
    }

    public ProductInfoResponse getProductInfo(String productId) {
        Product product = baseService.getProduct(productId);

        return ProductInfoResponse.from(product);
    }

    public List<ProductInfoResponse> getProductInfoFromShop(String shopId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<Product> products = productRepository.findAllByShop_Id(shopId, pageable);

        return products.stream()
                .map(ProductInfoResponse::from)
                .toList();
    }

    public List<ProductInfoInternResponse> getProductInfoIntern(String shopId, int page, int size) {
        var products = getProductInfoFromShop(shopId, page, size);

        return products.stream()
                .map(ProductInfoResponse::toIntern)
                .toList();
    }

    @Transactional
    public Product updateProduct(String productId, UpdateProductRequest updateRequest, String userId) {
        Product product = baseService.getProduct(productId);

        baseService.isUserHasAccessToProduct(productId, userId);

        product.setName(updateRequest.getName());
        product.setDescription(updateRequest.getDescription());
        product.setPrice(updateRequest.getPrice());
        product.setAttributes(updateRequest.getAttributes());

        ProductEvent productEvent = kafkaService.convertProductToEvent(product, ProductEvent.ProductStatus.UPDATED);
        kafkaProducerService.sendProductEvent(productEvent);

        return productRepository.save(product);
    }


    @Transactional
    public void updateProductRating(ReviewEvent reviewEvent) {

        var product = baseService.getProduct(reviewEvent.getProductId());

        if (!product.getShop().equals(product.getShop())) {
            throw new ShopAndProductDontLinkedException(reviewEvent.getProductId(), reviewEvent.getShopId());
        }

        product.setProductRating(reviewEvent.getAvgProductRating());
        product.setCountProductReviews(reviewEvent.getCountProductReviews());

        productRepository.save(product);
    }

    public void addToCart(String productId, int quantity, String userId) {
        var product = baseService.getProduct(productId);

        var request = AddToCartRequest.builder()
                .price(product.getPrice().doubleValue())
                .productId(productId).build();

        feignService.addToCart(userId, quantity, request);
    }
}
