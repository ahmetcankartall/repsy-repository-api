package com.repsy.repsy_repository_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {

    @Autowired
    private StorageStrategy storageStrategy; // Spring otomatik olarak uygun bean'i enjekte eder.

    public void store(MultipartFile file, String packageName, String version) {
        storageStrategy.store(file, packageName, version);
    }

    public byte[] load(String packageName, String version, String fileName) {
        return storageStrategy.load(packageName, version, fileName);
    }
} 