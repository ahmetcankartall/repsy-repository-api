package com.repsy.repsy_repository_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;

@Service
@ConditionalOnProperty(name = "storage.strategy", havingValue = "file-system")
public class FileSystemStorageStrategy implements StorageStrategy {

    @Value("${storage.file-system.root-dir:./storage}")
    private String rootDir;

    @Override
    public void store(MultipartFile file, String packageName, String version) {
        Path dirPath = Paths.get(rootDir, packageName, version);
        try {
            Files.createDirectories(dirPath);
            Files.copy(file.getInputStream(), dirPath.resolve(file.getOriginalFilename()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }
    }

    @Override
    public byte[] load(String packageName, String version, String fileName) {
        Path filePath = Paths.get(rootDir, packageName, version, fileName);
        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load file: " + e.getMessage());
        }
    }
} 