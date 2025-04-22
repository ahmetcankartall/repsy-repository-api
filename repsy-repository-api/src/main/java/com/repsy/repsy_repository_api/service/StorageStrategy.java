package com.repsy.repsy_repository_api.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageStrategy {
    void store(MultipartFile file, String packageName, String version);
    byte[] load(String packageName, String version, String fileName);
} 