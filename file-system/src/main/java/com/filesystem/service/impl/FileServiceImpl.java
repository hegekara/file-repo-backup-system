package com.filesystem.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.filesystem.service.IFileService;

@Service
public class FileServiceImpl implements IFileService {

    private final Path rootLocation = Paths.get("uploads");

    @Override
    public ResponseEntity<String> uploadFile(String entityType, Long id, MultipartFile file) {
        try {
            // Kullanıcı mı yoksa takım mı kontrol et
            Path directory = resolveEntityDirectory(id, entityType);

            // Klasörü oluştur ve dosyayı yükle
            Files.createDirectories(directory);
            Path filePath = directory.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("File uploaded successfully: " + filePath.toString());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error uploading file: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> deleteFile(String entityType, Long id, String fileName ) {
        try {
            Path directory = resolveEntityDirectory(id, entityType);
            Path filePath = directory.resolve(fileName);

            Files.deleteIfExists(filePath);
            return ResponseEntity.ok("File deleted successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error deleting file: " + e.getMessage());
        }
    }

    private Path resolveEntityDirectory(Long id, String entityType) {
        // entityType: "user" veya "team"
        return rootLocation.resolve(entityType + "-" + id);
    }
}