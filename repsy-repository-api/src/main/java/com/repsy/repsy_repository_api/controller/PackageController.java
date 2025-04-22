package com.repsy.repsy_repository_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repsy.repsy_repository_api.model.MetaDataDTO;
import com.repsy.repsy_repository_api.model.Package;
import com.repsy.repsy_repository_api.repository.PackageRepository;
import com.repsy.repsy_repository_api.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/")
public class PackageController {

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private StorageService storageService;

    @PostMapping("/{packageName}/{version}")
    public ResponseEntity<String> uploadPackage(
            @PathVariable String packageName,
            @PathVariable String version,
            @RequestParam("file") MultipartFile file) {

        // 1. Dosya adı kontrolü (package.rep veya meta.json olmalı)
        if (!file.getOriginalFilename().equals("package.rep") && 
            !file.getOriginalFilename().equals("meta.json")) {
            return ResponseEntity.badRequest().body("Only package.rep or meta.json files are allowed.");
        }

        try {
            // 2. Metadata işleme (meta.json ise)
            if (file.getOriginalFilename().equals("meta.json")) {
                ObjectMapper mapper = new ObjectMapper();
                MetaDataDTO metaData = mapper.readValue(file.getInputStream(), MetaDataDTO.class);

                Package packageEntity = new Package();
                packageEntity.setName(metaData.getName());
                packageEntity.setVersion(metaData.getVersion());
                packageEntity.setAuthor(metaData.getAuthor());
                packageRepository.save(packageEntity);
            }

            // 3. Dosyayı depolama katmanına kaydet
            storageService.store(file, packageName, version);

            return ResponseEntity.ok("File uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{packageName}/{version}/{fileName}")
    public ResponseEntity<byte[]> downloadPackage(
            @PathVariable String packageName,
            @PathVariable String version,
            @PathVariable String fileName) {

        // 1. Dosya adı kontrolü
        if (!fileName.equals("package.rep") && !fileName.equals("meta.json")) {
            return ResponseEntity.badRequest().body(null);
        }

        try {
            // 2. Depolama katmanından dosyayı getir
            byte[] fileContent = storageService.load(packageName, version, fileName);
            return ResponseEntity.ok().body(fileContent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
} 