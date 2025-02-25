package ru.feryafox.minio;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.web.multipart.MultipartFile;
import ru.feryafox.utils.HashUtils;

import java.io.InputStream;

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
    }

    public String uploadImage(MultipartFile file) throws Exception {
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
