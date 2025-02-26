package ru.feryafox.productservice.services;

import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.feryafox.productservice.entities.mongo.Image;
import ru.feryafox.productservice.entities.mongo.Product;
import ru.feryafox.productservice.entities.mongo.Shop;
import ru.feryafox.productservice.exceptions.ShopIsNotExist;
import ru.feryafox.productservice.models.requests.CreateProductRequest;
import ru.feryafox.productservice.models.responses.CreateProductResponse;
import ru.feryafox.productservice.models.responses.ProductInfoResponse;
import ru.feryafox.productservice.models.responses.UploadImageResponse;
import ru.feryafox.productservice.repositories.elastic.ProductSearchRepository;
import ru.feryafox.productservice.repositories.mongo.ImageRepository;
import ru.feryafox.productservice.repositories.mongo.ProductRepository;
import ru.feryafox.productservice.repositories.mongo.ShopRepository;
import ru.feryafox.productservice.services.feign.FeignService;
import ru.feryafox.productservice.services.minio.MinioService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ShopRepository shopRepository;
    private final BaseService baseService;
    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;
    private final MinioService minioService;
    private final ImageRepository imageRepository;
    private final FeignService feignService;

    @Transactional
    public CreateProductResponse createProduct(CreateProductRequest createProductRequest, String userId) {
        var shopOptional = shopRepository.findById(createProductRequest.getShopId());
        Shop shop;

        shop = shopOptional.orElseGet(() -> getAndSaveShopFromShopService(createProductRequest.getShopId()));

        baseService.isUserHasAccessToShop(shop, userId);

        Product product = Product.from(createProductRequest);

        product.setShop(shop);
        product.setUserCreate(userId);

        product = productRepository.save(product);
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

        ProductInfoResponse productInfoResponse = ProductInfoResponse.from(product);

        return productInfoResponse;
    }

    public List<ProductInfoResponse> getProductInfoFromShop(String shopId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<Product> products = productRepository.findAllByShop_Id(shopId, pageable);

        List<ProductInfoResponse> productInfoResponses = products.stream()
                .map(ProductInfoResponse::from)
                .toList();

        return productInfoResponses;
    }
}
