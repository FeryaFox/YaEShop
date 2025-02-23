package ru.feryafox.shopservice.services.minio;

import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.feryafox.utils.HashUtils;

import java.io.InputStream;

@Service
public class MinioService {
    private final MinioClient minioClient;
    private final String bucketName;
    private final String url;

    public MinioService(
            @Value("${minio.url}") String url,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey,
            @Value("${minio.bucket}") String bucketName) {

        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
        this.bucketName = bucketName;
        this.url = url;
    }

    public String uploadFile(MultipartFile file) throws Exception {
        byte[] fileBytes = file.getBytes();
        String fileHash = HashUtils.getSHA256Hash(fileBytes);
        String extension = getFileExtension(file.getOriginalFilename());
        String objectName = fileHash + extension;

        InputStream fileStream = file.getInputStream();
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(fileStream, file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        return url + "/" + bucketName + "/" + objectName;
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
