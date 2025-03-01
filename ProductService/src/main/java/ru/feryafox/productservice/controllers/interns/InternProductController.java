package ru.feryafox.productservice.controllers.interns;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.productservice.services.ProductService;

@RestController
@RequestMapping("/intern/product/")
@RequiredArgsConstructor
public class InternProductController {
    private final ProductService productService;

    @GetMapping("shop/{shopId}")
    public ResponseEntity<?> getStore(
            @PathVariable("shopId") String shopId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        var response = productService.getProductInfoIntern(shopId, page, size);
        return ResponseEntity.ok(response);
    }
}
