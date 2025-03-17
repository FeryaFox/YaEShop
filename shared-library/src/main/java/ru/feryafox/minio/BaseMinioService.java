package ru.feryafox.minio;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import ru.feryafox.utils.NewHashUtils;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class BaseMinioService {
    protected final MinioClient minioClient;
    protected final String bucketName;
    protected final String url;

    public BaseMinioService(
            String url,
            String accessKey,
            String secretKey,
            String bucketName
    ) {
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
        this.bucketName = bucketName;
        this.url = url;

        log.info("BaseMinioService инициализирован. URL: {}, Bucket: {}", url, bucketName);
    }

    public String uploadImage(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        try {
            byte[] fileBytes = file.getBytes();
            String fileHash = NewHashUtils.getSHA256Hash(fileBytes);
            String extension = getFileExtension(fileName);
            String objectName = fileHash + extension;

            log.info("Загрузка файла в Minio: {} (Хэш: {})", fileName, fileHash);

            try (InputStream fileStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(fileStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }

            String fileUrl = url + "/" + bucketName + "/" + objectName;
            log.info("Файл успешно загружен в Minio: {}", fileUrl);
            return fileUrl;
        } catch (MinioException e) {
            log.error("Ошибка Minio при загрузке файла {}: {}", fileName, e.getMessage(), e);
            throw new RuntimeException("Ошибка при загрузке файла в Minio", e);
        } catch (IOException e) {
            log.error("Ошибка ввода/вывода при обработке файла {}: {}", fileName, e.getMessage(), e);
            throw new RuntimeException("Ошибка при обработке файла", e);
        } catch (Exception e) {
            log.error("Неизвестная ошибка при загрузке файла {}: {}", fileName, e.getMessage(), e);
            throw new RuntimeException("Ошибка при загрузке файла", e);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
