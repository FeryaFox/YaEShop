package ru.feryafox.productservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.feryafox.productservice.entities.elastic.ProductIndex;
import ru.feryafox.productservice.entities.mongo.Image;
import ru.feryafox.productservice.entities.mongo.Product;
import ru.feryafox.productservice.entities.mongo.Shop;
import ru.feryafox.productservice.models.requests.CreateProductRequest;
import ru.feryafox.productservice.models.responses.CreateProductResponse;
import ru.feryafox.productservice.models.responses.UploadImageResponse;
import ru.feryafox.productservice.repositories.elastic.ProductSearchRepository;
import ru.feryafox.productservice.repositories.mongo.ImageRepository;
import ru.feryafox.productservice.repositories.mongo.ProductRepository;
import ru.feryafox.productservice.repositories.mongo.ShopRepository;
import ru.feryafox.productservice.services.minio.MinioService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ShopRepository shopRepository;
    private final BaseService baseService;
    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;
    private final MinioService minioService;
    private final ImageRepository imageRepository;

    public CreateProductResponse createProduct(CreateProductRequest createProductRequest, String userId) {
        Product product = Product.from(createProductRequest);
        Shop shop = baseService.getShop(createProductRequest.getShopId());


        product.setShop(shop);
        product.setUserCreate(userId);

        product = productRepository.save(product);
        // TODO добавить сюда Elasticsearch
//        ProductIndex productIndex = ProductIndex.of(product);
//        productSearchRepository.save(productIndex);

        return new CreateProductResponse(product.getId());
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
}
