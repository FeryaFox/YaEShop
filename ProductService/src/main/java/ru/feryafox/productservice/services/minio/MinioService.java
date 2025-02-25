package ru.feryafox.productservice.services.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.feryafox.minio.BaseMinioService;

@Service
public class MinioService extends BaseMinioService {
    public MinioService(
            @Value("${minio.url}") String url,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey,
            @Value("${minio.bucket}") String bucketName) {
        super(url, accessKey, secretKey, bucketName);
    }
}
