package com.repsy.repsy_repository_api.service;

import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@ConditionalOnProperty(name = "storage.strategy", havingValue = "object-storage")
public class ObjectStorageStrategy implements StorageStrategy {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Override
    public void store(MultipartFile file, String packageName, String version) {
        try {
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            // Bucket yoksa oluştur
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            // Dosyayı yükle (object name: {packageName}/{version}/{fileName})
            String objectName = packageName + "/" + version + "/" + file.getOriginalFilename();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("MinIO upload failed: " + e.getMessage());
        }
    }

    @Override
    public byte[] load(String packageName, String version, String fileName) {
        try {
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            String objectName = packageName + "/" + version + "/" + fileName;
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()).readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("MinIO download failed: " + e.getMessage());
        }
    }
}