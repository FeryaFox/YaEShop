package ru.feryafox.shopservice.services.minio;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.feryafox.minio.BaseMinioService;


@Service
@Slf4j
public class MinioService extends BaseMinioService {
    public MinioService(
            @Value("${minio.url}") String url,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey,
            @Value("${minio.bucket}") String bucketName) {
        super(url, accessKey, secretKey, bucketName);
        log.info("MinioService инициализирован. URL: {}, Bucket: {}", url, bucketName);
    }
}
