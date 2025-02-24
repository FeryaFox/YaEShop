package ru.feryafox.productservice.repositories.elastic;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import ru.feryafox.productservice.entities.elastic.ProductIndex;

import java.util.List;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductIndex, String> {
    List<ProductIndex> findByNameContaining(String name);
}
