package ru.feryafox.shopservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import ru.feryafox.models.internal.responses.ProductInfoInternResponse;

import java.util.List;

@FeignClient(name = "product-service")
public interface InternProductClient {
    @GetMapping("/intern/product/shop/{shopId}")
    List<ProductInfoInternResponse> getProductsByShopId(
            @PathVariable("shopId") String shopId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestHeader("X-Internal-API-Key") String apiKey
    );
}
