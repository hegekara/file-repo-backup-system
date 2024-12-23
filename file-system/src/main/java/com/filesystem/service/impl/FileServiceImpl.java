package com.filesystem.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.filesystem.service.IFileService;

@Service
public class FileServiceImpl implements IFileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    private final Path rootLocation = Paths.get("uploads");

    @Override
    public ResponseEntity<String> uploadFile(String entityType, Long id, MultipartFile file) {
        try {
            Path directory = resolveEntityDirectory(id, entityType);

            Files.createDirectories(directory);
            Path filePath = directory.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            logger.info("File uploaded successfully to: {}", filePath);
            return ResponseEntity.ok("File uploaded successfully: " + filePath.toString());
        } catch (IOException e) {
            logger.error("Error uploading file for entityType={} and id={}: {}", entityType, id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error uploading file: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Resource> downloadFile(String entityType, Long id, String fileName) {
        logger.info("File downloading started : entity : {} entity-id : {} file-name: {}", entityType, id, fileName);
        try {
            Path directory = resolveEntityDirectory(id, entityType);
            Path filePath = directory.resolve(fileName);

            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                logger.warn("File not found for entityType={}, id={}, fileName={}", entityType, id, fileName);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                    .body(null); // Dosya bulunamadı
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                logger.error("File exists but cannot be read for entityType={}, id={}, fileName={}", entityType, id, fileName);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body(null); // Dosya okunamıyor
            }

            logger.info("File downloaded successfully: {}", filePath);
            return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                                .body(resource);
        } catch (IOException e) {
            logger.error("Error downloading file for entityType={}, id={}, fileName={}: {}", entityType, id, fileName, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(null);
        }
    }

    @Override
    public ResponseEntity<String> deleteFile(String entityType, Long id, String fileName ) {
        try {
            Path directory = resolveEntityDirectory(id, entityType);
            Path filePath = directory.resolve(fileName);

            Files.deleteIfExists(filePath);
            logger.info("File deleted successfully: {}", filePath);
            return ResponseEntity.ok("File deleted successfully");
        } catch (IOException e) {
            logger.error("Error deleting file for entityType={}, id={}, fileName={}: {}", entityType, id, fileName, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error deleting file: " + e.getMessage());
        }
    }

    private Path resolveEntityDirectory(Long id, String entityType) {
        Path directory = rootLocation.resolve(entityType + "-" + id);
        logger.debug("Resolved directory for entityType={}, id={}: {}", entityType, id, directory);
        return directory;
    }
}