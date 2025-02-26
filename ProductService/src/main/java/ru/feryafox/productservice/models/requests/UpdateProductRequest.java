package ru.feryafox.productservice.models.requests;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class UpdateProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Map<String, String> attributes;
}
