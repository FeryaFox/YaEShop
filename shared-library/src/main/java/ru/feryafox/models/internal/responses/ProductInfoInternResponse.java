package ru.feryafox.models.internal.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductInfoInternResponse {
    private String id;
    private String name;
    private String description;
    private String shopId;
    private Set<ImageInternResponse> images;
    private Map<String, String> attributes;
    private double price;
    private double rating;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ImageInternResponse {
        private String url;
        private int position;
    }
}
