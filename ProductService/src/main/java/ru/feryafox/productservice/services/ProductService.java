package ru.feryafox.productservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feryafox.productservice.entities.elastic.ProductIndex;
import ru.feryafox.productservice.entities.mongo.Product;
import ru.feryafox.productservice.entities.mongo.Shop;
import ru.feryafox.productservice.models.requests.CreateProductRequest;
import ru.feryafox.productservice.models.responses.CreateProductResponse;
import ru.feryafox.productservice.repositories.elastic.ProductSearchRepository;
import ru.feryafox.productservice.repositories.mongo.ProductRepository;
import ru.feryafox.productservice.repositories.mongo.ShopRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ShopRepository shopRepository;
    private final BaseService baseService;
    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;

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
}
