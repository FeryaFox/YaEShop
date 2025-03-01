package ru.feryafox.shopservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feryafox.models.internal.responses.ProductInfoInternResponse;
import ru.feryafox.shopservice.services.feign.FeignService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductsService {
    private final FeignService feignService;

    public List<ProductInfoInternResponse> getInternProducts(String shopId, int page, int size) {
        return feignService.getProducts(shopId, page, size);
    }
}
