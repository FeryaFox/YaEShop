package ru.feryafox.shopservice.services.feign;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.feryafox.models.internal.responses.ProductInfoInternResponse;
import ru.feryafox.shopservice.feign.InternProductClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeignService {
    @Value("internal.api.key")
    private String apiKey;

    private final InternProductClient internProductClient;

    public List<ProductInfoInternResponse> getProducts(String shopId, int page, int size) {
        return internProductClient.getProductsByShopId(shopId, page, size, apiKey);
    }
}
